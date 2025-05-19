SELECT
    P.ParticipantID,
    P.FirstName,
    P.MiddleName,
    P.LastName,
    P.LastName || ', ' || P.FirstName || (CASE WHEN P.MiddleName IS NOT NULL AND P.MiddleName <> '' THEN ' ' || P.MiddleName ELSE '' END) AS ParticipantFullName,
    P.Phone,
    P.Email,
    L1.LocationName AS PickUpLocation,
    L2.LocationName AS DropOffLocation,
    P.JoinDate,
    P.Program,
    P.BenefitAmount,
    P.Active
FROM
    Participants AS P
        LEFT JOIN
    Locations AS L1 ON P.PickUpLocationID_FK = L1.LocationID
        LEFT JOIN
    Locations AS L2 ON P.DropOffLocationID_FK = L2.LocationID
ORDER BY
    P.LastName, P.FirstName;