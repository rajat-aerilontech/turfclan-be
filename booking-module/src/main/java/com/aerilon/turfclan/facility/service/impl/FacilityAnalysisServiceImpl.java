package com.aerilon.turfclan.facility.service.impl;

import com.aerilon.turfclan.entity.BookingEntity;
import com.aerilon.turfclan.enums.BookingStatus;
import com.aerilon.turfclan.enums.Sports;
import com.aerilon.turfclan.exception.InvalidRequestException;
import com.aerilon.turfclan.exception.ResourceNotFoundException;
import com.aerilon.turfclan.exception.UnauthorizedAccessException;
import com.aerilon.turfclan.facility.dto.FacilityAnalysisResponseDto;
import com.aerilon.turfclan.facility.entity.FacilityEntity;
import com.aerilon.turfclan.facility.entity.SubFacilityEntity;
import com.aerilon.turfclan.facility.service.FacilityAnalysisService;
import com.aerilon.turfclan.partner.repository.FacilityRepository;
import com.aerilon.turfclan.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FacilityAnalysisServiceImpl implements FacilityAnalysisService {

    private static final Set<BookingStatus> REVENUE_STATUSES = Set.of(
            BookingStatus.CONFIRMED,
            BookingStatus.COMPLETED
    );

    private static final Set<BookingStatus> ACTIVE_USAGE_STATUSES = Set.of(
            BookingStatus.CONFIRMED,
            BookingStatus.COMPLETED,
            BookingStatus.PENDING_PAYMENT,
            BookingStatus.PENDING_APPROVAL
    );

    private static final Set<BookingStatus> DROPOFF_STATUSES = Set.of(
            BookingStatus.CANCELLED,
            BookingStatus.REJECTED,
            BookingStatus.EXPIRED
    );

    private final FacilityRepository facilityRepository;
    private final BookingRepository bookingRepository;

    @Override
    public FacilityAnalysisResponseDto getFacilityAnalysis(String userId, UUID facilityId,
                                                           LocalDate startDate, LocalDate endDate) {
        UUID userUuid;
        try {
            userUuid = UUID.fromString(userId);
        } catch (Exception ex) {
            throw new UnauthorizedAccessException("Unauthorized: invalid user");
        }

        LocalDate effectiveEnd = endDate != null ? endDate : LocalDate.now();
        LocalDate effectiveStart = startDate != null ? startDate : effectiveEnd.minusDays(29);
        if (effectiveStart.isAfter(effectiveEnd)) {
            throw new InvalidRequestException("startDate must be before or equal to endDate");
        }

        FacilityEntity facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Facility not found"));

        if (facility.getUser() == null || !facility.getUser().getId().equals(userUuid)) {
            throw new UnauthorizedAccessException("Unauthorized: Facility does not belong to this user");
        }

        List<BookingEntity> bookings = bookingRepository.findByFacilityAndBookingDateBetween(
                facilityId, effectiveStart, effectiveEnd);

        FacilityAnalysisResponseDto response = new FacilityAnalysisResponseDto();
        response.setFacilityId(facilityId);
        response.setStartDate(effectiveStart);
        response.setEndDate(effectiveEnd);
        response.setGeneratedAt(LocalDateTime.now());

        if (bookings.isEmpty()) {
            response.setBookingVolumeRevenue(emptyBookingVolume());
            response.setPeakUsage(emptyPeakUsage());
            response.setPrimeTimePerformance(emptyPrimePerformance());
            response.setDropOffRates(emptyDropOffRates());
            response.setLeadTime(emptyLeadTime());
            response.setSportMix(emptySportMix());
            response.setOccupancy(emptyOccupancy());
            response.setGeoPerformance(emptyGeoPerformance(facility));
            return response;
        }

        AnalysisAccumulator accumulator = new AnalysisAccumulator(effectiveStart, effectiveEnd);

        Map<UUID, List<TimeRange>> primeWindowsBySport = buildPrimeWindowMap(facility);
        Map<UUID, SubFacilityEntity> sportById = facility.getSubFacility() == null
                ? Map.of()
                : facility.getSubFacility().stream().collect(Collectors.toMap(SubFacilityEntity::getId, s -> s));

        for (BookingEntity booking : bookings) {
            accumulator.accept(booking, primeWindowsBySport, sportById);
        }

        AvailabilityAccumulator availability = computeAvailability(facility, effectiveStart, effectiveEnd, primeWindowsBySport);
        response.setBookingVolumeRevenue(accumulator.toBookingVolumeRevenue());
        response.setPeakUsage(accumulator.toPeakUsage());
        response.setPrimeTimePerformance(accumulator.toPrimeTimePerformance(availability));
        response.setDropOffRates(accumulator.toDropOffRates());
        response.setLeadTime(accumulator.toLeadTime());
        response.setSportMix(accumulator.toSportMix());
        response.setOccupancy(accumulator.toOccupancy());
        response.setGeoPerformance(accumulator.toGeoPerformance(facility));

        return response;
    }

    private Map<UUID, List<TimeRange>> buildPrimeWindowMap(FacilityEntity facility) {
        Map<UUID, List<TimeRange>> primeWindowsBySport = new HashMap<>();
        if (facility.getSubFacility() == null) {
            return primeWindowsBySport;
        }
        for (SubFacilityEntity sport : facility.getSubFacility()) {
            primeWindowsBySport.put(sport.getId(), parsePrimeWindows(sport.getPrimeTimeWindows()));
        }
        return primeWindowsBySport;
    }

    private AvailabilityAccumulator computeAvailability(FacilityEntity facility,
                                                        LocalDate startDate,
                                                        LocalDate endDate,
                                                        Map<UUID, List<TimeRange>> primeWindowsBySport) {
        AvailabilityAccumulator availability = new AvailabilityAccumulator();
        if (facility.getSubFacility() == null || facility.getSubFacility().isEmpty()) {
            return availability;
        }

        LocalDate date = startDate;
        while (!date.isAfter(endDate)) {
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            for (SubFacilityEntity sport : facility.getSubFacility()) {
                if (!isAvailableOnDay(sport, dayOfWeek)) {
                    continue;
                }
                List<TimeRange> windows = primeWindowsBySport.getOrDefault(sport.getId(), List.of());
                SlotCount slots = countSlotsForDay(sport, windows);
                int units = sport.getNumberOfUnits() != null ? sport.getNumberOfUnits() : 0;
                availability.primeAvailableSlots += slots.primeSlots * units;
                availability.nonPrimeAvailableSlots += slots.nonPrimeSlots * units;
            }
            date = date.plusDays(1);
        }

        return availability;
    }

    private boolean isAvailableOnDay(SubFacilityEntity sport, DayOfWeek dayOfWeek) {
        Set<String> availableDays = sport.getAvailableDays();
        if (availableDays == null || availableDays.isEmpty()) {
            return true;
        }
        return availableDays.contains(dayOfWeek.name());
    }

    private SlotCount countSlotsForDay(SubFacilityEntity sport, List<TimeRange> windows) {
        if (sport.getOpenTime() == null || sport.getCloseTime() == null || sport.getSlotDurationMinutes() == null) {
            return new SlotCount(0, 0);
        }
        LocalTime current = sport.getOpenTime();
        LocalTime closeTime = sport.getCloseTime();
        int slotMinutes = sport.getSlotDurationMinutes();
        int bufferMinutes = sport.getBufferDuration() != null ? sport.getBufferDuration() : 0;
        long primeSlots = 0;
        long nonPrimeSlots = 0;

        while (current.isBefore(closeTime)) {
            LocalTime slotEnd = current.plusMinutes(slotMinutes);
            if (slotEnd.isAfter(closeTime)) {
                break;
            }
            if (isPrimeTime(windows, current)) {
                primeSlots++;
            } else {
                nonPrimeSlots++;
            }
            current = slotEnd.plusMinutes(bufferMinutes);
        }
        return new SlotCount(primeSlots, nonPrimeSlots);
    }

    private List<TimeRange> parsePrimeWindows(Set<String> windows) {
        if (windows == null || windows.isEmpty()) {
            return List.of();
        }
        List<TimeRange> ranges = new ArrayList<>();
        for (String window : windows) {
            if (window == null || window.isBlank() || !window.contains("-")) {
                continue;
            }
            String[] parts = window.split("-");
            if (parts.length != 2) {
                continue;
            }
            try {
                LocalTime start = LocalTime.parse(parts[0].trim());
                LocalTime end = LocalTime.parse(parts[1].trim());
                ranges.add(new TimeRange(start, end));
            } catch (Exception ex) {
                log.warn("Invalid prime time window format: {}", window);
            }
        }
        return ranges;
    }

    private static boolean isPrimeTime(List<TimeRange> windows, LocalTime time) {
        if (windows == null || windows.isEmpty() || time == null) {
            return false;
        }
        for (TimeRange range : windows) {
            if (!time.isBefore(range.start) && time.isBefore(range.end)) {
                return true;
            }
        }
        return false;
    }

    private FacilityAnalysisResponseDto.BookingVolumeRevenueDto emptyBookingVolume() {
        FacilityAnalysisResponseDto.BookingVolumeRevenueDto dto = new FacilityAnalysisResponseDto.BookingVolumeRevenueDto();
        dto.setDailyTrend(List.of());
        dto.setWeeklyTrend(List.of());
        dto.setMonthlyTrend(List.of());
        return dto;
    }

    private FacilityAnalysisResponseDto.PeakUsageDto emptyPeakUsage() {
        FacilityAnalysisResponseDto.PeakUsageDto dto = new FacilityAnalysisResponseDto.PeakUsageDto();
        dto.setTopHours(List.of());
        dto.setTopDaysOfWeek(List.of());
        dto.setHeatmap(List.of());
        return dto;
    }

    private FacilityAnalysisResponseDto.PrimeTimePerformanceDto emptyPrimePerformance() {
        FacilityAnalysisResponseDto.PrimeTimePerformanceDto dto = new FacilityAnalysisResponseDto.PrimeTimePerformanceDto();
        dto.setPrimeTime(new FacilityAnalysisResponseDto.PerformanceBucketDto());
        dto.setNonPrimeTime(new FacilityAnalysisResponseDto.PerformanceBucketDto());
        return dto;
    }

    private FacilityAnalysisResponseDto.DropOffRatesDto emptyDropOffRates() {
        FacilityAnalysisResponseDto.DropOffRatesDto dto = new FacilityAnalysisResponseDto.DropOffRatesDto();
        dto.setTopReasons(List.of());
        dto.setDailyTrend(List.of());
        return dto;
    }

    private FacilityAnalysisResponseDto.LeadTimeDto emptyLeadTime() {
        FacilityAnalysisResponseDto.LeadTimeDto dto = new FacilityAnalysisResponseDto.LeadTimeDto();
        dto.setDistribution(List.of());
        return dto;
    }

    private FacilityAnalysisResponseDto.SportMixDto emptySportMix() {
        FacilityAnalysisResponseDto.SportMixDto dto = new FacilityAnalysisResponseDto.SportMixDto();
        dto.setBySport(List.of());
        dto.setTopSubtypes(List.of());
        dto.setSeasonalShifts(List.of());
        return dto;
    }

    private FacilityAnalysisResponseDto.OccupancyDto emptyOccupancy() {
        FacilityAnalysisResponseDto.OccupancyDto dto = new FacilityAnalysisResponseDto.OccupancyDto();
        dto.setBusiestSportsByOccupancy(List.of());
        return dto;
    }

    private FacilityAnalysisResponseDto.GeoPerformanceDto emptyGeoPerformance(FacilityEntity facility) {
        FacilityAnalysisResponseDto.GeoPerformanceDto dto = new FacilityAnalysisResponseDto.GeoPerformanceDto();
        FacilityAnalysisResponseDto.GeoMetricDto metric = new FacilityAnalysisResponseDto.GeoMetricDto();
        metric.setCity(facility.getCity());
        metric.setState(facility.getState());
        metric.setBookings(0);
        metric.setRevenue(0);
        dto.setByCityState(List.of(metric));
        dto.setTopCities(List.of(metric));
        dto.setCityTrends(List.of());
        return dto;
    }

    private static class AnalysisAccumulator {
        private final LocalDate startDate;
        private final LocalDate endDate;
        private final WeekFields weekFields = WeekFields.of(Locale.getDefault());

        private long totalBookings;
        private long confirmedBookings;
        private long cancelledBookings;
        private double totalRevenue;

        private final Map<LocalDate, TrendAccumulator> dailyTrend = new HashMap<>();
        private final Map<String, TrendAccumulator> weeklyTrend = new HashMap<>();
        private final Map<YearMonth, TrendAccumulator> monthlyTrend = new HashMap<>();

        private final Map<Integer, Long> hourCounts = new HashMap<>();
        private final Map<DayOfWeek, Long> dayCounts = new HashMap<>();
        private final Map<DayOfWeek, Map<Integer, Long>> heatmap = new HashMap<>();

        private long primeBookingCount;
        private long primeRevenueBookingCount;
        private double primeRevenue;

        private long nonPrimeBookingCount;
        private long nonPrimeRevenueBookingCount;
        private double nonPrimeRevenue;

        private final Map<LocalDate, DropOffAccumulator> dropOffDaily = new HashMap<>();

        private long leadTimeCount;
        private long sameDayCount;
        private double leadTimeSum;
        private long leadBucket0to1;
        private long leadBucket2to3;
        private long leadBucket4to7;
        private long leadBucket8plus;

        private double playersSum;
        private long playersCount;
        private double capacityUsedSum;
        private long capacityCount;

        private final Map<Sports, SportAccumulator> sportTotals = new HashMap<>();
        private final Map<String, SportAccumulator> subtypeTotals = new HashMap<>();
        private final Map<Sports, Map<YearMonth, SportAccumulator>> sportMonthly = new HashMap<>();
        private final Map<Sports, OccupancyAccumulator> sportOccupancy = new HashMap<>();

        private final Map<LocalDate, Double> dailyRevenue = new HashMap<>();

        private AnalysisAccumulator(LocalDate startDate, LocalDate endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }

        private void accept(BookingEntity booking,
                            Map<UUID, List<TimeRange>> primeWindowsBySport,
                            Map<UUID, SubFacilityEntity> sportById) {
            if (booking == null || booking.getBookingDate() == null || booking.getBookingStatus() == null) {
                return;
            }

            totalBookings++;
            LocalDate date = booking.getBookingDate();
            BookingStatus status = booking.getBookingStatus();

            double price = booking.getFinalPrice() != null ? booking.getFinalPrice() : 0;
            boolean isRevenue = REVENUE_STATUSES.contains(status);
            if (isRevenue) {
                confirmedBookings++;
                totalRevenue += price;
                dailyRevenue.merge(date, price, Double::sum);
            }
            if (status == BookingStatus.CANCELLED) {
                cancelledBookings++;
            }

            updateTrend(dailyTrend, date, status, price);
            updateWeeklyTrend(date, status, price);
            updateMonthlyTrend(date, status, price);

            if (ACTIVE_USAGE_STATUSES.contains(status)) {
                updatePeakUsage(date, booking.getStartTime());
            }

            UUID sportId = booking.getSport() != null ? booking.getSport().getId() : null;
            List<TimeRange> windows = sportId != null ? primeWindowsBySport.get(sportId) : List.of();
            boolean prime = isPrimeTime(windows, booking.getStartTime());
            if (ACTIVE_USAGE_STATUSES.contains(status)) {
                if (prime) {
                    primeBookingCount++;
                } else {
                    nonPrimeBookingCount++;
                }
            }
            if (isRevenue) {
                if (prime) {
                    primeRevenueBookingCount++;
                    primeRevenue += price;
                } else {
                    nonPrimeRevenueBookingCount++;
                    nonPrimeRevenue += price;
                }
            }

            if (DROPOFF_STATUSES.contains(status)) {
                DropOffAccumulator dropOff = dropOffDaily.computeIfAbsent(date, d -> new DropOffAccumulator());
                if (status == BookingStatus.CANCELLED) {
                    dropOff.cancelled++;
                } else if (status == BookingStatus.REJECTED) {
                    dropOff.rejected++;
                } else if (status == BookingStatus.EXPIRED) {
                    dropOff.expired++;
                }
            }

            updateLeadTime(booking);
            updateSportMix(booking, price, isRevenue);
            updateOccupancy(booking, sportById.get(sportId));
        }

        private void updateTrend(Map<LocalDate, TrendAccumulator> target,
                                 LocalDate date,
                                 BookingStatus status,
                                 double revenue) {
            TrendAccumulator acc = target.computeIfAbsent(date, d -> new TrendAccumulator());
            acc.total++;
            if (REVENUE_STATUSES.contains(status)) {
                acc.confirmed++;
                acc.revenue += revenue;
            }
            if (status == BookingStatus.CANCELLED) {
                acc.cancelled++;
            }
        }

        private void updateWeeklyTrend(LocalDate date, BookingStatus status, double revenue) {
            int week = date.get(weekFields.weekOfWeekBasedYear());
            int year = date.get(weekFields.weekBasedYear());
            String key = String.format("%d-W%02d", year, week);
            TrendAccumulator acc = weeklyTrend.computeIfAbsent(key, d -> new TrendAccumulator());
            acc.total++;
            if (REVENUE_STATUSES.contains(status)) {
                acc.confirmed++;
                acc.revenue += revenue;
            }
            if (status == BookingStatus.CANCELLED) {
                acc.cancelled++;
            }
        }

        private void updateMonthlyTrend(LocalDate date, BookingStatus status, double revenue) {
            YearMonth month = YearMonth.from(date);
            TrendAccumulator acc = monthlyTrend.computeIfAbsent(month, d -> new TrendAccumulator());
            acc.total++;
            if (REVENUE_STATUSES.contains(status)) {
                acc.confirmed++;
                acc.revenue += revenue;
            }
            if (status == BookingStatus.CANCELLED) {
                acc.cancelled++;
            }
        }

        private void updatePeakUsage(LocalDate date, LocalTime startTime) {
            if (startTime == null) {
                return;
            }
            int hour = startTime.getHour();
            hourCounts.merge(hour, 1L, Long::sum);
            DayOfWeek day = date.getDayOfWeek();
            dayCounts.merge(day, 1L, Long::sum);
            heatmap.computeIfAbsent(day, d -> new HashMap<>()).merge(hour, 1L, Long::sum);
        }

        private void updateLeadTime(BookingEntity booking) {
            if (booking.getCreatedAt() == null || booking.getBookingDate() == null) {
                return;
            }
            long leadDays = ChronoUnit.DAYS.between(booking.getCreatedAt().toLocalDate(), booking.getBookingDate());
            if (leadDays < 0) {
                leadDays = 0;
            }
            leadTimeCount++;
            leadTimeSum += leadDays;
            if (leadDays == 0) {
                sameDayCount++;
            }
            if (leadDays <= 1) {
                leadBucket0to1++;
            } else if (leadDays <= 3) {
                leadBucket2to3++;
            } else if (leadDays <= 7) {
                leadBucket4to7++;
            } else {
                leadBucket8plus++;
            }
        }

        private void updateSportMix(BookingEntity booking, double price, boolean isRevenue) {
            if (booking.getSport() == null || booking.getSport().getSportType() == null) {
                return;
            }
            Sports sportType = booking.getSport().getSportType();
            SportAccumulator sportAcc = sportTotals.computeIfAbsent(sportType, s -> new SportAccumulator());
            sportAcc.bookings++;
            if (isRevenue) {
                sportAcc.revenue += price;
            }

            String subType = booking.getSport().getSubType();
            String subtypeKey = sportType + "|" + (subType != null ? subType : "UNKNOWN");
            SportAccumulator subtypeAcc = subtypeTotals.computeIfAbsent(subtypeKey, s -> new SportAccumulator());
            subtypeAcc.bookings++;
            if (isRevenue) {
                subtypeAcc.revenue += price;
            }

            YearMonth month = YearMonth.from(booking.getBookingDate());
            Map<YearMonth, SportAccumulator> byMonth = sportMonthly.computeIfAbsent(sportType, s -> new HashMap<>());
            SportAccumulator monthAcc = byMonth.computeIfAbsent(month, m -> new SportAccumulator());
            monthAcc.bookings++;
            if (isRevenue) {
                monthAcc.revenue += price;
            }
        }

        private void updateOccupancy(BookingEntity booking, SubFacilityEntity sport) {
            if (booking.getPlayerCount() != null) {
                playersSum += booking.getPlayerCount();
                playersCount++;
            }
            if (sport != null && sport.getMaxPlayersPerUnit() != null && sport.getMaxPlayersPerUnit() > 0
                    && booking.getPlayerCount() != null) {
                double ratio = booking.getPlayerCount() / (double) sport.getMaxPlayersPerUnit();
                capacityUsedSum += ratio;
                capacityCount++;

                Sports sportType = sport.getSportType();
                if (sportType != null) {
                    OccupancyAccumulator acc = sportOccupancy.computeIfAbsent(sportType, s -> new OccupancyAccumulator());
                    acc.capacitySum += ratio;
                    acc.bookings++;
                }
            }
        }

        private FacilityAnalysisResponseDto.BookingVolumeRevenueDto toBookingVolumeRevenue() {
            FacilityAnalysisResponseDto.BookingVolumeRevenueDto dto = new FacilityAnalysisResponseDto.BookingVolumeRevenueDto();
            dto.setTotalBookings(totalBookings);
            dto.setConfirmedBookings(confirmedBookings);
            dto.setCancelledBookings(cancelledBookings);
            dto.setTotalRevenue(totalRevenue);
            dto.setAverageRevenuePerBooking(confirmedBookings > 0 ? totalRevenue / confirmedBookings : 0);
            dto.setDailyTrend(toDailyTrendPoints());
            dto.setWeeklyTrend(toWeeklyTrendPoints());
            dto.setMonthlyTrend(toMonthlyTrendPoints());
            return dto;
        }

        private List<FacilityAnalysisResponseDto.TrendPointDto> toDailyTrendPoints() {
            List<FacilityAnalysisResponseDto.TrendPointDto> points = new ArrayList<>();
            LocalDate date = startDate;
            while (!date.isAfter(endDate)) {
                TrendAccumulator acc = dailyTrend.getOrDefault(date, new TrendAccumulator());
                FacilityAnalysisResponseDto.TrendPointDto point = new FacilityAnalysisResponseDto.TrendPointDto();
                point.setBucket(date.toString());
                point.setTotalBookings(acc.total);
                point.setConfirmedBookings(acc.confirmed);
                point.setCancelledBookings(acc.cancelled);
                point.setTotalRevenue(acc.revenue);
                points.add(point);
                date = date.plusDays(1);
            }
            return points;
        }

        private List<FacilityAnalysisResponseDto.TrendPointDto> toWeeklyTrendPoints() {
            Map<Integer, String> ordering = new LinkedHashMap<>();
            LocalDate date = startDate;
            while (!date.isAfter(endDate)) {
                int week = date.get(weekFields.weekOfWeekBasedYear());
                int year = date.get(weekFields.weekBasedYear());
                int key = year * 100 + week;
                ordering.putIfAbsent(key, String.format("%d-W%02d", year, week));
                date = date.plusDays(7);
            }
            List<FacilityAnalysisResponseDto.TrendPointDto> points = new ArrayList<>();
            ordering.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> {
                        TrendAccumulator acc = weeklyTrend.getOrDefault(entry.getValue(), new TrendAccumulator());
                        FacilityAnalysisResponseDto.TrendPointDto point = new FacilityAnalysisResponseDto.TrendPointDto();
                        point.setBucket(entry.getValue());
                        point.setTotalBookings(acc.total);
                        point.setConfirmedBookings(acc.confirmed);
                        point.setCancelledBookings(acc.cancelled);
                        point.setTotalRevenue(acc.revenue);
                        points.add(point);
                    });
            return points;
        }

        private List<FacilityAnalysisResponseDto.TrendPointDto> toMonthlyTrendPoints() {
            List<YearMonth> months = new ArrayList<>();
            YearMonth current = YearMonth.from(startDate);
            YearMonth end = YearMonth.from(endDate);
            while (!current.isAfter(end)) {
                months.add(current);
                current = current.plusMonths(1);
            }
            List<FacilityAnalysisResponseDto.TrendPointDto> points = new ArrayList<>();
            for (YearMonth month : months) {
                TrendAccumulator acc = monthlyTrend.getOrDefault(month, new TrendAccumulator());
                FacilityAnalysisResponseDto.TrendPointDto point = new FacilityAnalysisResponseDto.TrendPointDto();
                point.setBucket(month.toString());
                point.setTotalBookings(acc.total);
                point.setConfirmedBookings(acc.confirmed);
                point.setCancelledBookings(acc.cancelled);
                point.setTotalRevenue(acc.revenue);
                points.add(point);
            }
            return points;
        }

        private FacilityAnalysisResponseDto.PeakUsageDto toPeakUsage() {
            FacilityAnalysisResponseDto.PeakUsageDto dto = new FacilityAnalysisResponseDto.PeakUsageDto();
            dto.setTopHours(toTopHours());
            dto.setTopDaysOfWeek(toTopDays());
            dto.setHeatmap(toHeatmap());
            dto.setBusiestDateRange(toBusiestDateRange());
            return dto;
        }

        private List<FacilityAnalysisResponseDto.CountByHourDto> toTopHours() {
            return hourCounts.entrySet().stream()
                    .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
                    .limit(3)
                    .map(entry -> {
                        FacilityAnalysisResponseDto.CountByHourDto dto = new FacilityAnalysisResponseDto.CountByHourDto();
                        dto.setHour(entry.getKey());
                        dto.setBookings(entry.getValue());
                        return dto;
                    })
                    .collect(Collectors.toList());
        }

        private List<FacilityAnalysisResponseDto.CountByDayDto> toTopDays() {
            return dayCounts.entrySet().stream()
                    .sorted(Map.Entry.<DayOfWeek, Long>comparingByValue().reversed())
                    .limit(3)
                    .map(entry -> {
                        FacilityAnalysisResponseDto.CountByDayDto dto = new FacilityAnalysisResponseDto.CountByDayDto();
                        dto.setDayOfWeek(entry.getKey().name());
                        dto.setBookings(entry.getValue());
                        return dto;
                    })
                    .collect(Collectors.toList());
        }

        private List<FacilityAnalysisResponseDto.HeatmapCellDto> toHeatmap() {
            List<FacilityAnalysisResponseDto.HeatmapCellDto> cells = new ArrayList<>();
            for (Map.Entry<DayOfWeek, Map<Integer, Long>> dayEntry : heatmap.entrySet()) {
                for (Map.Entry<Integer, Long> hourEntry : dayEntry.getValue().entrySet()) {
                    FacilityAnalysisResponseDto.HeatmapCellDto cell = new FacilityAnalysisResponseDto.HeatmapCellDto();
                    cell.setDayOfWeek(dayEntry.getKey().name());
                    cell.setHour(hourEntry.getKey());
                    cell.setBookings(hourEntry.getValue());
                    cells.add(cell);
                }
            }
            cells.sort(Comparator.comparing(FacilityAnalysisResponseDto.HeatmapCellDto::getDayOfWeek)
                    .thenComparing(FacilityAnalysisResponseDto.HeatmapCellDto::getHour));
            return cells;
        }

        private FacilityAnalysisResponseDto.BusiestDateRangeDto toBusiestDateRange() {
            FacilityAnalysisResponseDto.BusiestDateRangeDto dto = new FacilityAnalysisResponseDto.BusiestDateRangeDto();
            List<LocalDate> dates = new ArrayList<>();
            LocalDate date = startDate;
            while (!date.isAfter(endDate)) {
                dates.add(date);
                date = date.plusDays(1);
            }
            if (dates.isEmpty()) {
                return dto;
            }
            int windowSize = Math.min(30, dates.size());
            long currentCount = 0;
            double currentRevenue = 0;
            for (int i = 0; i < windowSize; i++) {
                LocalDate d = dates.get(i);
                TrendAccumulator acc = dailyTrend.getOrDefault(d, new TrendAccumulator());
                currentCount += acc.total;
                currentRevenue += dailyRevenue.getOrDefault(d, 0.0);
            }
            long bestCount = currentCount;
            double bestRevenue = currentRevenue;
            int bestStartIndex = 0;

            for (int i = windowSize; i < dates.size(); i++) {
                LocalDate removeDate = dates.get(i - windowSize);
                TrendAccumulator removeAcc = dailyTrend.getOrDefault(removeDate, new TrendAccumulator());
                currentCount -= removeAcc.total;
                currentRevenue -= dailyRevenue.getOrDefault(removeDate, 0.0);

                LocalDate addDate = dates.get(i);
                TrendAccumulator addAcc = dailyTrend.getOrDefault(addDate, new TrendAccumulator());
                currentCount += addAcc.total;
                currentRevenue += dailyRevenue.getOrDefault(addDate, 0.0);

                if (currentCount > bestCount) {
                    bestCount = currentCount;
                    bestRevenue = currentRevenue;
                    bestStartIndex = i - windowSize + 1;
                }
            }

            dto.setStartDate(dates.get(bestStartIndex));
            dto.setEndDate(dates.get(bestStartIndex + windowSize - 1));
            dto.setTotalBookings(bestCount);
            dto.setTotalRevenue(bestRevenue);
            return dto;
        }

        private FacilityAnalysisResponseDto.PrimeTimePerformanceDto toPrimeTimePerformance(AvailabilityAccumulator availability) {
            FacilityAnalysisResponseDto.PrimeTimePerformanceDto dto = new FacilityAnalysisResponseDto.PrimeTimePerformanceDto();
            FacilityAnalysisResponseDto.PerformanceBucketDto prime = new FacilityAnalysisResponseDto.PerformanceBucketDto();
            prime.setBookingCount(primeBookingCount);
            prime.setRevenue(primeRevenue);
            prime.setAveragePrice(primeRevenueBookingCount > 0 ? primeRevenue / primeRevenueBookingCount : 0);
            prime.setOccupancyRate(availability.primeAvailableSlots > 0
                    ? (double) primeBookingCount / availability.primeAvailableSlots
                    : 0);

            FacilityAnalysisResponseDto.PerformanceBucketDto nonPrime = new FacilityAnalysisResponseDto.PerformanceBucketDto();
            nonPrime.setBookingCount(nonPrimeBookingCount);
            nonPrime.setRevenue(nonPrimeRevenue);
            nonPrime.setAveragePrice(nonPrimeRevenueBookingCount > 0 ? nonPrimeRevenue / nonPrimeRevenueBookingCount : 0);
            nonPrime.setOccupancyRate(availability.nonPrimeAvailableSlots > 0
                    ? (double) nonPrimeBookingCount / availability.nonPrimeAvailableSlots
                    : 0);

            dto.setPrimeTime(prime);
            dto.setNonPrimeTime(nonPrime);
            return dto;
        }

        private FacilityAnalysisResponseDto.DropOffRatesDto toDropOffRates() {
            FacilityAnalysisResponseDto.DropOffRatesDto dto = new FacilityAnalysisResponseDto.DropOffRatesDto();
            long cancelled = cancelledBookings;
            long rejected = 0;
            long expired = 0;
            for (DropOffAccumulator acc : dropOffDaily.values()) {
                rejected += acc.rejected;
                expired += acc.expired;
            }
            dto.setTotalBookings(totalBookings);
            dto.setCancelledCount(cancelled);
            dto.setRejectedCount(rejected);
            dto.setExpiredCount(expired);
            dto.setCancellationRate(totalBookings > 0 ? (cancelled * 100.0) / totalBookings : 0);
            dto.setRejectionRate(totalBookings > 0 ? (rejected * 100.0) / totalBookings : 0);
            dto.setExpiryRate(totalBookings > 0 ? (expired * 100.0) / totalBookings : 0);
            dto.setTopReasons(List.of());
            dto.setDailyTrend(toDropOffDailyTrend());
            return dto;
        }

        private List<FacilityAnalysisResponseDto.DropOffTrendPointDto> toDropOffDailyTrend() {
            List<FacilityAnalysisResponseDto.DropOffTrendPointDto> points = new ArrayList<>();
            LocalDate date = startDate;
            while (!date.isAfter(endDate)) {
                DropOffAccumulator acc = dropOffDaily.getOrDefault(date, new DropOffAccumulator());
                FacilityAnalysisResponseDto.DropOffTrendPointDto point = new FacilityAnalysisResponseDto.DropOffTrendPointDto();
                point.setBucket(date.toString());
                point.setCancelledCount(acc.cancelled);
                point.setRejectedCount(acc.rejected);
                point.setExpiredCount(acc.expired);
                points.add(point);
                date = date.plusDays(1);
            }
            return points;
        }

        private FacilityAnalysisResponseDto.LeadTimeDto toLeadTime() {
            FacilityAnalysisResponseDto.LeadTimeDto dto = new FacilityAnalysisResponseDto.LeadTimeDto();
            dto.setAverageLeadTimeDays(leadTimeCount > 0 ? leadTimeSum / leadTimeCount : 0);
            dto.setSameDayPercentage(leadTimeCount > 0 ? (sameDayCount * 100.0) / leadTimeCount : 0);
            dto.setDistribution(toLeadTimeBuckets());
            return dto;
        }

        private List<FacilityAnalysisResponseDto.BucketCountDto> toLeadTimeBuckets() {
            long total = leadBucket0to1 + leadBucket2to3 + leadBucket4to7 + leadBucket8plus;
            List<FacilityAnalysisResponseDto.BucketCountDto> buckets = new ArrayList<>();
            buckets.add(buildBucket("0-1 days", leadBucket0to1, total));
            buckets.add(buildBucket("2-3 days", leadBucket2to3, total));
            buckets.add(buildBucket("4-7 days", leadBucket4to7, total));
            buckets.add(buildBucket("8+ days", leadBucket8plus, total));
            return buckets;
        }

        private FacilityAnalysisResponseDto.BucketCountDto buildBucket(String label, long count, long total) {
            FacilityAnalysisResponseDto.BucketCountDto dto = new FacilityAnalysisResponseDto.BucketCountDto();
            dto.setBucket(label);
            dto.setCount(count);
            dto.setPercentage(total > 0 ? (count * 100.0) / total : 0);
            return dto;
        }

        private FacilityAnalysisResponseDto.SportMixDto toSportMix() {
            FacilityAnalysisResponseDto.SportMixDto dto = new FacilityAnalysisResponseDto.SportMixDto();
            dto.setBySport(toSportMetrics());
            dto.setTopSubtypes(toSubtypeMetrics());
            dto.setSeasonalShifts(toSeasonalShifts());
            return dto;
        }

        private List<FacilityAnalysisResponseDto.SportMetricDto> toSportMetrics() {
            return sportTotals.entrySet().stream()
                    .map(entry -> {
                        FacilityAnalysisResponseDto.SportMetricDto dto = new FacilityAnalysisResponseDto.SportMetricDto();
                        dto.setSportType(entry.getKey().name());
                        dto.setBookings(entry.getValue().bookings);
                        dto.setRevenue(entry.getValue().revenue);
                        return dto;
                    })
                    .sorted(Comparator.comparing(FacilityAnalysisResponseDto.SportMetricDto::getBookings).reversed())
                    .collect(Collectors.toList());
        }

        private List<FacilityAnalysisResponseDto.SubtypeMetricDto> toSubtypeMetrics() {
            return subtypeTotals.entrySet().stream()
                    .map(entry -> {
                        String[] parts = entry.getKey().split("\\|", 2);
                        FacilityAnalysisResponseDto.SubtypeMetricDto dto = new FacilityAnalysisResponseDto.SubtypeMetricDto();
                        dto.setSportType(parts[0]);
                        dto.setSubType(parts.length > 1 ? parts[1] : "UNKNOWN");
                        dto.setBookings(entry.getValue().bookings);
                        dto.setRevenue(entry.getValue().revenue);
                        return dto;
                    })
                    .sorted(Comparator.comparing(FacilityAnalysisResponseDto.SubtypeMetricDto::getBookings).reversed())
                    .limit(5)
                    .collect(Collectors.toList());
        }

        private List<FacilityAnalysisResponseDto.SportMonthlyTrendDto> toSeasonalShifts() {
            return sportMonthly.entrySet().stream()
                    .map(entry -> {
                        FacilityAnalysisResponseDto.SportMonthlyTrendDto dto = new FacilityAnalysisResponseDto.SportMonthlyTrendDto();
                        dto.setSportType(entry.getKey().name());
                        List<FacilityAnalysisResponseDto.MonthMetricDto> monthly = entry.getValue().entrySet().stream()
                                .sorted(Map.Entry.comparingByKey())
                                .map(monthEntry -> {
                                    FacilityAnalysisResponseDto.MonthMetricDto monthDto = new FacilityAnalysisResponseDto.MonthMetricDto();
                                    monthDto.setMonth(monthEntry.getKey().toString());
                                    monthDto.setBookings(monthEntry.getValue().bookings);
                                    monthDto.setRevenue(monthEntry.getValue().revenue);
                                    return monthDto;
                                })
                                .collect(Collectors.toList());
                        dto.setMonthlyTrend(monthly);
                        return dto;
                    })
                    .collect(Collectors.toList());
        }

        private FacilityAnalysisResponseDto.OccupancyDto toOccupancy() {
            FacilityAnalysisResponseDto.OccupancyDto dto = new FacilityAnalysisResponseDto.OccupancyDto();
            dto.setAveragePlayersPerBooking(playersCount > 0 ? playersSum / playersCount : 0);
            dto.setAverageCapacityUsedPercent(capacityCount > 0 ? (capacityUsedSum / capacityCount) * 100.0 : 0);
            dto.setBusiestSportsByOccupancy(toSportOccupancy());
            return dto;
        }

        private List<FacilityAnalysisResponseDto.SportOccupancyDto> toSportOccupancy() {
            return sportOccupancy.entrySet().stream()
                    .map(entry -> {
                        OccupancyAccumulator acc = entry.getValue();
                        FacilityAnalysisResponseDto.SportOccupancyDto dto = new FacilityAnalysisResponseDto.SportOccupancyDto();
                        dto.setSportType(entry.getKey().name());
                        dto.setAverageCapacityUsedPercent(acc.bookings > 0 ? (acc.capacitySum / acc.bookings) * 100.0 : 0);
                        dto.setBookings(acc.bookings);
                        return dto;
                    })
                    .sorted(Comparator.comparing(FacilityAnalysisResponseDto.SportOccupancyDto::getAverageCapacityUsedPercent).reversed())
                    .collect(Collectors.toList());
        }

        private FacilityAnalysisResponseDto.GeoPerformanceDto toGeoPerformance(FacilityEntity facility) {
            FacilityAnalysisResponseDto.GeoPerformanceDto dto = new FacilityAnalysisResponseDto.GeoPerformanceDto();
            FacilityAnalysisResponseDto.GeoMetricDto metric = new FacilityAnalysisResponseDto.GeoMetricDto();
            metric.setCity(facility.getCity());
            metric.setState(facility.getState());
            metric.setBookings(totalBookings);
            metric.setRevenue(totalRevenue);
            dto.setByCityState(List.of(metric));
            dto.setTopCities(List.of(metric));
            dto.setCityTrends(toCityTrends(facility));
            return dto;
        }

        private List<FacilityAnalysisResponseDto.GeoTrendPointDto> toCityTrends(FacilityEntity facility) {
            List<FacilityAnalysisResponseDto.GeoTrendPointDto> points = new ArrayList<>();
            LocalDate date = startDate;
            while (!date.isAfter(endDate)) {
                TrendAccumulator acc = dailyTrend.getOrDefault(date, new TrendAccumulator());
                FacilityAnalysisResponseDto.GeoTrendPointDto point = new FacilityAnalysisResponseDto.GeoTrendPointDto();
                point.setCity(facility.getCity());
                point.setState(facility.getState());
                point.setBucket(date.toString());
                point.setBookings(acc.total);
                point.setRevenue(acc.revenue);
                points.add(point);
                date = date.plusDays(1);
            }
            return points;
        }
    }

    private static class TrendAccumulator {
        private long total;
        private long confirmed;
        private long cancelled;
        private double revenue;
    }

    private static class DropOffAccumulator {
        private long cancelled;
        private long rejected;
        private long expired;
    }

    private static class SportAccumulator {
        private long bookings;
        private double revenue;
    }

    private static class OccupancyAccumulator {
        private long bookings;
        private double capacitySum;
    }

    private static class TimeRange {
        private final LocalTime start;
        private final LocalTime end;

        private TimeRange(LocalTime start, LocalTime end) {
            this.start = start;
            this.end = end;
        }
    }

    private static class SlotCount {
        private final long primeSlots;
        private final long nonPrimeSlots;

        private SlotCount(long primeSlots, long nonPrimeSlots) {
            this.primeSlots = primeSlots;
            this.nonPrimeSlots = nonPrimeSlots;
        }
    }

    private static class AvailabilityAccumulator {
        private long primeAvailableSlots;
        private long nonPrimeAvailableSlots;
    }
}
