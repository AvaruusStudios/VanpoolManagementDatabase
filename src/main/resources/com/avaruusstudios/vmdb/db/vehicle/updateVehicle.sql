UPDATE Vehicles
SET
    VehicleNumber = ?,
    Make = ?,
    Model = ?,
    Year = ?,
    Capacity = ?,
    LeaseStartDate = ?,
    LeaseEndDate = ?,
    Discount = ?,
    IsActive = ?,
    DeletedAt = ?,
    Notes = ?
WHERE
    VehicleID = ?;