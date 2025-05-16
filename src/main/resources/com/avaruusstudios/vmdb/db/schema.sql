-- 🚐 Vehicles
CREATE TABLE Vehicles (
    VehicleID INTEGER PRIMARY KEY AUTOINCREMENT,
    VehicleNumber TEXT NOT NULL,
    Make TEXT,
    Model TEXT,
    Year INTEGER,
    Capacity INTEGER NOT NULL,
    LeaseStartDate TEXT NOT NULL, -- ISO format recommended
    LeaseEndDate TEXT,
    Active INTEGER NOT NULL DEFAULT 1, -- 1=True, 0=False
    SubsidyAmount REAL DEFAULT 0,
    Notes TEXT
);

-- 📍 Locations
CREATE TABLE Locations (
    LocationID INTEGER PRIMARY KEY AUTOINCREMENT,
    LocationName TEXT NOT NULL,
    Address TEXT,
    City TEXT,
    State TEXT,
    ZipCode TEXT,
    Notes TEXT
);

-- 👥 Participants
CREATE TABLE Participants (
    ParticipantID INTEGER PRIMARY KEY AUTOINCREMENT,
    FirstName TEXT NOT NULL,
    LastName TEXT NOT NULL,
    MiddleName TEXT,
    Email TEXT NOT NULL,
    Phone TEXT,
    pickUpLocationID_FK INTEGER,
    dropOffLocationID_FK INTEGER,
    DistanceMiles REAL NOT NULL,
    JoinDate TEXT NOT NULL,
    Active INTEGER NOT NULL DEFAULT 1,
    Notes TEXT,
    FOREIGN KEY (pickUpLocationID_FK) REFERENCES Locations(LocationID),
    FOREIGN KEY (dropOffLocationID_FK) REFERENCES Locations(LocationID)
);

-- 🧾 Invoices
CREATE TABLE Invoices (
    InvoiceID INTEGER PRIMARY KEY AUTOINCREMENT,
    InvoiceDate TEXT NOT NULL,
    DueDate TEXT NOT NULL,
    PeriodLabel TEXT NOT NULL,
    VehicleID_FK INTEGER NOT NULL,
    Notes TEXT,
    FOREIGN KEY (VehicleID_FK) REFERENCES Vehicles(VehicleID)
);

-- 💸 Invoice Details
CREATE TABLE InvoiceDetails (
    InvoiceDetailID INTEGER PRIMARY KEY AUTOINCREMENT,
    InvoiceID_FK INTEGER NOT NULL,
    ParticipantID_FK INTEGER NOT NULL,
    AmountDue REAL NOT NULL,
    PaidAmount REAL DEFAULT 0,
    IsPaid INTEGER NOT NULL DEFAULT 0,
    Notes TEXT,
    FOREIGN KEY (InvoiceID_FK) REFERENCES Invoices(InvoiceID),
    FOREIGN KEY (ParticipantID_FK) REFERENCES Participants(ParticipantID)
);

-- 💳 Categories
CREATE TABLE Categories (
    CategoryID INTEGER PRIMARY KEY AUTOINCREMENT,
    CategoryName TEXT NOT NULL,
    CategoryType TEXT NOT NULL CHECK (CategoryType IN ('Income', 'Expense', 'Credit')),
    ParticipantID_FK INTEGER,
    Description TEXT,
    FOREIGN KEY (ParticipantID_FK) REFERENCES Participants(ParticipantID)
);

-- 📒 Transactions
CREATE TABLE Transactions (
    TransactionID INTEGER PRIMARY KEY AUTOINCREMENT,
    TransactionDate TEXT NOT NULL,
    VehicleID_FK INTEGER NOT NULL,
    CategoryID_FK INTEGER NOT NULL,
    Amount REAL NOT NULL,
    PaymentMethod TEXT,
    InvoiceID_FK INTEGER,
    Notes TEXT,
    FOREIGN KEY (VehicleID_FK) REFERENCES Vehicles(VehicleID),
    FOREIGN KEY (CategoryID_FK) REFERENCES Categories(CategoryID),
    FOREIGN KEY (InvoiceID_FK) REFERENCES Invoices(InvoiceID)
);

-- 📝 Event Log
CREATE TABLE EventLog (
    EventID INTEGER PRIMARY KEY AUTOINCREMENT,
    EventDate TEXT NOT NULL,
    EventType TEXT NOT NULL CHECK (EventType IN ('INSERT', 'UPDATE', 'DELETE', 'ERROR')),
    TableName TEXT NOT NULL,
    RecordID INTEGER,
    ErrorCode INTEGER,
    Description TEXT,
    UserName TEXT NOT NULL
);

-- 👤 Users
CREATE TABLE Users (
    UserID INTEGER PRIMARY KEY AUTOINCREMENT,
    WindowsUsername VARCHAR(255) UNIQUE NOT NULL,
    FirstName VARCHAR(255),
    MiddleName VARCHAR(255),
    LastName VARCHAR(255),
    Email VARCHAR(255),
    UserRole VARCHAR(50) NOT NULL, -- e.g., "ADMIN", "TREASURER", "USER"
    IsActive INTEGER NOT NULL DEFAULT 1, -- Assuming 1 for TRUE, 0 for FALSE
    DateCreated TEXT DEFAULT CURRENT_TIMESTAMP -- SQLite uses TEXT for DATETIME and CURRENT_TIMESTAMP
);