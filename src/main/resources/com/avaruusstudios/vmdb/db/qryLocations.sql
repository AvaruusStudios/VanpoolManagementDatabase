SELECT
    L.LocationID,
    L.LocationName,
    L.Address,
    L.City,
    L.State,
    L.ZipCode,
    L.Latitude,
    L.Longitude
FROM
    Locations AS L
ORDER BY
    L.LocationName;