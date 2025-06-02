SELECT
    Transactions.TransactionID,
    Transactions.VehicleID_FK,
    Transactions.CategoryID_FK,
    Transactions.InvoiceID_FK,
    Transactions.ParticipantID_FK,
    Transactions.TransactionDate,
    Transactions.Amount,
    Transactions.PaymentMethod,
    Transactions.Notes
FROM
    Transactions
ORDER BY
    Transactions.TransactionID;