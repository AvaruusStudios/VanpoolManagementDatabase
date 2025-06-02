SELECT
    Locations.LocationID,
    Locations.LocationName,
    Locations.Address,
    Locations.City,
    Locations.State,
    Locations.ZipCode,
    Locations.Latitude,
    Locations.Longitude,
    Locations.Notes
FROM
    Locations
ORDER BY
    Locations.LocationName;