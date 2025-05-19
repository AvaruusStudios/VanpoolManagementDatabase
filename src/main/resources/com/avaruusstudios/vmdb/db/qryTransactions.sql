SELECT
    T.TransactionID,
    T.TransactionDate AS Date,
    C.CategoryType AS Type,
    C.CategoryName AS Category,
    T.Amount,
    T.PaymentMethod AS Method,
    T.Notes AS Details
FROM
    Categories AS C
    INNER JOIN
    Transactions AS T ON C.CategoryID = T.CategoryID_FK
ORDER BY
    T.TransactionID;
