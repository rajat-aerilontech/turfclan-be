package com.aerilon.turfclan.enums;

public enum BookingStatus {
    // Standard Flow
    PENDING_PAYMENT,    // User is at checkout; slot is temporarily held
    CONFIRMED,          // Payment successful or Partner approved cash booking

    // Cash / Pay-at-Venue Flow
    PENDING_APPROVAL,   // "Pay in Cash" request waiting for Partner to Accept/Reject

    // Cancellation/Failure
    REJECTED,           // Partner declined a cash booking
    CANCELLED,          // User or System cancelled the booking
    EXPIRED,            // Payment window timed out

    // Administrative
    BLOCKED,            // Partner manually blocked the turf (e.g., maintenance)
    COMPLETED           // Match has been played
}
