SELECT
    Vehicles.VehicleID,
    Vehicles.VehicleNumber,
    Vehicles.Make,
    Vehicles.Model,
    Vehicles.Year,
    Vehicles.Capacity,
    Vehicles.LeaseStartDate,
    Vehicles.LeaseEndDate,
    Vehicles.Discount,
    Vehicles.IsActive,
    Vehicles.DeletedAt,
    Vehicles.Notes
FROM
    Vehicles
WHERE
    Vehicles.IsActive = 1;