SELECT
    Invoices.InvoiceID,
    Invoices.VehicleID_FK,
    Invoices.InvoiceDate,
    Invoices.DueDate,
    Invoices.PeriodLabel,
    Invoices.Notes
FROM
    Invoices
ORDER BY
    Invoices.InvoiceDate DESC;