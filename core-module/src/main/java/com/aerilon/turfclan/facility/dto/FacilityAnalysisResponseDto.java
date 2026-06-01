package com.aerilon.turfclan.facility.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class FacilityAnalysisResponseDto {

    private UUID facilityId;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime generatedAt;

    private BookingVolumeRevenueDto bookingVolumeRevenue;
    private PeakUsageDto peakUsage;
    private PrimeTimePerformanceDto primeTimePerformance;
    private DropOffRatesDto dropOffRates;
    private LeadTimeDto leadTime;
    private SportMixDto sportMix;
    private OccupancyDto occupancy;
    private GeoPerformanceDto geoPerformance;

    @Getter
    @Setter
    public static class BookingVolumeRevenueDto {
        private long totalBookings;
        private long confirmedBookings;
        private long cancelledBookings;
        private double totalRevenue;
        private double averageRevenuePerBooking;
        private List<TrendPointDto> dailyTrend;
        private List<TrendPointDto> weeklyTrend;
        private List<TrendPointDto> monthlyTrend;
    }

    @Getter
    @Setter
    public static class TrendPointDto {
        private String bucket;
        private long totalBookings;
        private long confirmedBookings;
        private long cancelledBookings;
        private double totalRevenue;
    }

    @Getter
    @Setter
    public static class PeakUsageDto {
        private List<CountByHourDto> topHours;
        private List<CountByDayDto> topDaysOfWeek;
        private List<HeatmapCellDto> heatmap;
        private BusiestDateRangeDto busiestDateRange;
    }

    @Getter
    @Setter
    public static class CountByHourDto {
        private int hour;
        private long bookings;
    }

    @Getter
    @Setter
    public static class CountByDayDto {
        private String dayOfWeek;
        private long bookings;
    }

    @Getter
    @Setter
    public static class HeatmapCellDto {
        private String dayOfWeek;
        private int hour;
        private long bookings;
    }

    @Getter
    @Setter
    public static class BusiestDateRangeDto {
        private LocalDate startDate;
        private LocalDate endDate;
        private long totalBookings;
        private double totalRevenue;
    }

    @Getter
    @Setter
    public static class PrimeTimePerformanceDto {
        private PerformanceBucketDto primeTime;
        private PerformanceBucketDto nonPrimeTime;
    }

    @Getter
    @Setter
    public static class PerformanceBucketDto {
        private long bookingCount;
        private double revenue;
        private double averagePrice;
        private double occupancyRate;
    }

    @Getter
    @Setter
    public static class DropOffRatesDto {
        private long totalBookings;
        private long cancelledCount;
        private long rejectedCount;
        private long expiredCount;
        private double cancellationRate;
        private double rejectionRate;
        private double expiryRate;
        private List<ReasonCountDto> topReasons;
        private List<DropOffTrendPointDto> dailyTrend;
    }

    @Getter
    @Setter
    public static class ReasonCountDto {
        private String reason;
        private long count;
    }

    @Getter
    @Setter
    public static class DropOffTrendPointDto {
        private String bucket;
        private long cancelledCount;
        private long rejectedCount;
        private long expiredCount;
    }

    @Getter
    @Setter
    public static class LeadTimeDto {
        private double averageLeadTimeDays;
        private double sameDayPercentage;
        private List<BucketCountDto> distribution;
    }

    @Getter
    @Setter
    public static class BucketCountDto {
        private String bucket;
        private long count;
        private double percentage;
    }

    @Getter
    @Setter
    public static class SportMixDto {
        private List<SportMetricDto> bySport;
        private List<SubtypeMetricDto> topSubtypes;
        private List<SportMonthlyTrendDto> seasonalShifts;
    }

    @Getter
    @Setter
    public static class SportMetricDto {
        private String sportType;
        private long bookings;
        private double revenue;
    }

    @Getter
    @Setter
    public static class SubtypeMetricDto {
        private String sportType;
        private String subType;
        private long bookings;
        private double revenue;
    }

    @Getter
    @Setter
    public static class SportMonthlyTrendDto {
        private String sportType;
        private List<MonthMetricDto> monthlyTrend;
    }

    @Getter
    @Setter
    public static class MonthMetricDto {
        private String month;
        private long bookings;
        private double revenue;
    }

    @Getter
    @Setter
    public static class OccupancyDto {
        private double averagePlayersPerBooking;
        private double averageCapacityUsedPercent;
        private List<SportOccupancyDto> busiestSportsByOccupancy;
    }

    @Getter
    @Setter
    public static class SportOccupancyDto {
        private String sportType;
        private double averageCapacityUsedPercent;
        private long bookings;
    }

    @Getter
    @Setter
    public static class GeoPerformanceDto {
        private List<GeoMetricDto> byCityState;
        private List<GeoMetricDto> topCities;
        private List<GeoTrendPointDto> cityTrends;
    }

    @Getter
    @Setter
    public static class GeoMetricDto {
        private String city;
        private String state;
        private long bookings;
        private double revenue;
    }

    @Getter
    @Setter
    public static class GeoTrendPointDto {
        private String city;
        private String state;
        private String bucket;
        private long bookings;
        private double revenue;
    }
}
