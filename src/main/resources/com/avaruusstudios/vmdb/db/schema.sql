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
    Latitude INTEGER,
    Longitude INTEGER,
    Notes TEXT
);

-- 👥 Participants
CREATE TABLE Participants (
    ParticipantID INTEGER PRIMARY KEY AUTOINCREMENT,
    PickUpLocationID_FK INTEGER,
    DropOffLocationID_FK INTEGER,
    FirstName TEXT NOT NULL,
    MiddleName TEXT,
    LastName TEXT NOT NULL,
    Email TEXT NOT NULL,
    Phone TEXT,
    DistanceMiles REAL NOT NULL,
    JoinDate TEXT NOT NULL,
    Active INTEGER NOT NULL DEFAULT 1,
    Program TEXT,
    BenefitAmount REAL DEFAULT 0,
    Notes TEXT,
    FOREIGN KEY (PickUpLocationID_FK) REFERENCES Locations(LocationID),
    FOREIGN KEY (DropOffLocationID_FK) REFERENCES Locations(LocationID)
);

-- 🧾 Invoices
CREATE TABLE Invoices (
    InvoiceID INTEGER PRIMARY KEY AUTOINCREMENT,
    VehicleID_FK INTEGER NOT NULL,
    InvoiceDate TEXT NOT NULL,
    DueDate TEXT NOT NULL,
    PeriodLabel TEXT NOT NULL,
    Notes TEXT,
    FOREIGN KEY (VehicleID_FK) REFERENCES Vehicles(VehicleID)
);

-- 💸 Invoice Items
CREATE TABLE InvoiceItems (
    InvoiceItemID INTEGER PRIMARY KEY AUTOINCREMENT,
    InvoiceID_FK INTEGER NOT NULL,
    ParticipantID_FK INTEGER NOT NULL,
    BenefitPayment REAL NOT NULL,
    PersonalPayment REAL NOT NULL,
    PaidAmount REAL DEFAULT 0,
    IsPaid INTEGER NOT NULL DEFAULT 0,
    Notes TEXT,
    FOREIGN KEY (InvoiceID_FK) REFERENCES Invoices(InvoiceID),
    FOREIGN KEY (ParticipantID_FK) REFERENCES Participants(ParticipantID)
);

-- 💳 Categories
CREATE TABLE Categories (
    CategoryID INTEGER PRIMARY KEY AUTOINCREMENT,
    ParticipantID_FK INTEGER,
    CategoryName TEXT NOT NULL,
    CategoryType TEXT NOT NULL CHECK (CategoryType IN ('Income', 'Expense', 'Credit')),
    Description TEXT,
    FOREIGN KEY (ParticipantID_FK) REFERENCES Participants(ParticipantID)
);

-- 📒 Transactions
CREATE TABLE Transactions (
    TransactionID INTEGER PRIMARY KEY AUTOINCREMENT,
    VehicleID_FK INTEGER NOT NULL,
    CategoryID_FK INTEGER NOT NULL,
    InvoiceID_FK INTEGER,
    TransactionDate TEXT NOT NULL,
    Amount REAL NOT NULL,
    PaymentMethod TEXT,
    Notes TEXT,
    FOREIGN KEY (VehicleID_FK) REFERENCES Vehicles(VehicleID),
    FOREIGN KEY (CategoryID_FK) REFERENCES Categories(CategoryID),
    FOREIGN KEY (InvoiceID_FK) REFERENCES Invoices(InvoiceID)
);

-- 📝 Event Log
CREATE TABLE EventLog (
    EventID INTEGER PRIMARY KEY AUTOINCREMENT,
    UserID_FK INTEGER NOT NULL,
    EventDate TEXT NOT NULL,
    EventType TEXT NOT NULL CHECK (EventType IN ('INSERT', 'UPDATE', 'DELETE', 'ERROR')),
    TableName TEXT NOT NULL,
    RecordID INTEGER,
    ErrorCode INTEGER,
    Description TEXT,
    FOREIGN KEY (UserID_FK) REFERENCES Users(UserID)
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
    Active INTEGER NOT NULL DEFAULT 1, -- Assuming 1 for TRUE, 0 for FALSE
    DateCreated TEXT DEFAULT CURRENT_TIMESTAMP -- SQLite uses TEXT for DATETIME and CURRENT_TIMESTAMP
);