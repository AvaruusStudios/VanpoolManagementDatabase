INSERT INTO Vehicles (
    VehicleNumber,
    Make,
    Model,
    Year,
    Capacity,
    LeaseStartDate,
    LeaseEndDate,
    Discount,
    IsActive,
    DeletedAt,
    Notes)
VALUES
    (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);