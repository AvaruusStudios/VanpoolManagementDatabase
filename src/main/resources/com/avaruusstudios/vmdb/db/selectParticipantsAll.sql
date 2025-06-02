SELECT
    Participants.ParticipantID,
    Participants.FirstName,
    Participants.MiddleName,
    Participants.LastName,
    Participants.Phone,
    Participants.Email,
    Participants.PickUpLocationID_FK,
    Participants.DropOffLocationID_FK,
    Participants.DistanceMiles,
    Participants.JoinDate,
    Participants.Program,
    Participants.BenefitAmount,
    Participants.IsActive,
    Participants.Notes
FROM
    Participants
ORDER BY
    Participants.LastName, Participants.FirstName;