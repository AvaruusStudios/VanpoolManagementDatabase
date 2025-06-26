SELECT
    Participants.ParticipantID AS ID,
    Participants.LastName || ', ' || Participants.FirstName || ' ' || COALESCE(SUBSTR(Participants.MiddleName, 1, 1), '') AS Participant,
    Participants.Program,
    Participants.IsActive
FROM
    Participants;