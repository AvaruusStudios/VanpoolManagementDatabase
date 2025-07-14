UPDATE Vehicles
SET
    IsActive = ?,
    DeletedAt = ?
WHERE
    VehicleID = ?;