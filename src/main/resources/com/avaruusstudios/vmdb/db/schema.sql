-- 🚐 Vehicles
CREATE TABLE Vehicles (
    VehicleID INTEGER PRIMARY KEY AUTOINCREMENT,        -- Vehicle Identifier
    VehicleNumber TEXT UNIQUE NOT NULL,                        -- Vehicle Number issues by Rental Agency
    LicensePlate TEXT UNIQUE NOT NULL,
    Make TEXT,                                          -- Vehicle Make
    Model TEXT,                                         -- Vehicle Model
    Year INTEGER,                                       -- Vehicle Year
    Capacity INTEGER NOT NULL,                          -- Vehicle Seating Capacity
    LeaseStartDate TEXT NOT NULL,                       -- Lease Start Date in ISO format recommended
    LeaseEndDate TEXT,                                  -- Lease End Date in ISO format recommended
    Discount NUMERIC DEFAULT 0,                         -- Discount amount
    IsActive INTEGER NOT NULL DEFAULT 1,                -- 1=True, 0=False
    DeletedAt TEXT DEFAULT NULL,
    Notes TEXT,                                          -- Contextual Notes about the Vehicle
    DateCreated TEXT DEFAULT CURRENT_TIMESTAMP
);

-- 📍 Locations
CREATE TABLE Locations (
    LocationID INTEGER PRIMARY KEY AUTOINCREMENT,       -- Location Identifier
    LocationName TEXT NOT NULL,                         -- Location Name
    Address TEXT NOT NULL,                              -- Location number and street
    City TEXT NOT NULL,                                 -- Location City
    State TEXT NOT NULL,                                -- Location State
    ZipCode TEXT NOT NULL,                              -- Location Zip-Code
    Latitude REAL,                                      -- Location Latitude
    Longitude REAL,                                     -- Location Longitude
    IsActive INTEGER NOT NULL DEFAULT 1,                -- 1=True, 0=False
    DeletedAt TEXT DEFAULT NULL,
    Notes TEXT,                                          -- Contextual Notes about the Location
    DateCreated TEXT DEFAULT CURRENT_TIMESTAMP
);

-- 👥 Participants
CREATE TABLE Participants (
    ParticipantID INTEGER PRIMARY KEY AUTOINCREMENT,                                                    -- Participant Identifier
    PickUpLocationID_FK INTEGER,                                                                        -- Place the Participant is picked from in morning (Foreign Key)
    DropOffLocationID_FK INTEGER,                                                                       -- Place the Participant is dropped off for work in the morning (Foreign Key)
    FirstName TEXT NOT NULL,                                                                            -- Participant First Name
    MiddleName TEXT,                                                                                    -- Participant Middle Name
    LastName TEXT NOT NULL,                                                                             -- Participant Last Name
    Email TEXT UNIQUE NOT NULL,                                                                                -- Participant Email Address
    Phone TEXT,                                                                                         -- Participant Phone Number
    DistanceMiles NUMERIC NOT NULL,                                                                     -- Distance in Miles from Pickup Location to Drop-Off Location
    JoinDate TEXT NOT NULL,                                                                             -- Date Participant Joined the Vanpoool
    Program TEXT NOT NULL CHECK (Program IN ('TRANSPORTATION_INCENTIVE_PROGRAM', 'DAILY', 'NONE')),     -- Program the Participant is associated with
    BenefitAmount NUMERIC DEFAULT 0,                                                                    -- Benefit Amount the Participant receives each month
    IsActive INTEGER NOT NULL DEFAULT 1,                                                                -- 1=True, 0=False
    DeletedAt TEXT DEFAULT NULL,
    Notes TEXT,                                                                                         -- Contextual Notes about the Participant
    DateCreated TEXT DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (PickUpLocationID_FK) REFERENCES Locations(LocationID),
    FOREIGN KEY (DropOffLocationID_FK) REFERENCES Locations(LocationID)
);

-- 🧾 Invoices
CREATE TABLE Invoices (
    InvoiceID INTEGER PRIMARY KEY AUTOINCREMENT,                                                 -- Invoice Identifier
    VehicleID_FK INTEGER NOT NULL,                                                               -- Vehicle Tied to the Invoice (Foreign Key)
    InvoiceType TEXT NOT NULL DEFAULT 'NONE' CHECK (InvoiceType IN ('LEASE', 'FUEL', 'NONE')),   -- The type of Invoice
    InvoiceDate TEXT NOT NULL,                                                                   -- Date the Invoice was created
    DueDate TEXT NOT NULL,                                                                       -- Date the Participants are required to pay their share of the Invoice
    PeriodLabel TEXT NOT NULL,                                                                   -- Coverage period of the Invoice (eg. MMM YYYY)
    PaymentStatus TEXT NOT NULL DEFAULT 'UNPAID' CHECK (PaymentStatus IN ('UNPAID', 'PARTIALLY_PAID', 'PAID', 'OVERDUE', 'CANCELLED')), -- Payment status of the Invoice
    Notes TEXT,                                                                                  -- Contextual Notes about the Invoice
    DateCreated TEXT DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (VehicleID_FK) REFERENCES Vehicles(VehicleID)
);

-- 💸 Invoice Items
CREATE TABLE LineItems (
    LineItemID INTEGER PRIMARY KEY AUTOINCREMENT,                           -- Invoice Item Identifier
    InvoiceID_FK INTEGER NOT NULL,                                          -- Invoice the Invoice Item belongs to
    ParticipantID_FK INTEGER NOT NULL,                                      -- Every Invoice Item is for a specific Participant
    BenefitPayment NUMERIC NOT NULL,                                        -- The amount the Participant is required to pay to their Benefit Card
    PersonalPayment NUMERIC NOT NULL,                                       -- The amount the Participant is required to pay to their personal credit card
    IsPaid INTEGER NOT NULL DEFAULT 0,                                      -- Did the Participant make their payment?
    Notes TEXT,                                                             -- Contextual Notes about the Invoice Item
    DateCreated TEXT DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (InvoiceID_FK) REFERENCES Invoices(InvoiceID),
    FOREIGN KEY (ParticipantID_FK) REFERENCES Participants(ParticipantID)
);

-- 💳 Categories
CREATE TABLE Categories (
    CategoryID INTEGER PRIMARY KEY AUTOINCREMENT,                                       -- Category Identifier
    ParticipantID_FK INTEGER,                                                           -- Participant Identifier (Foreign Key) Ties a Participant to a Category Name
    CategoryType TEXT NOT NULL CHECK (CategoryType IN ('INCOME', 'EXPENSE', 'CREDIT', 'NONE')), -- Category Types
    CategoryName TEXT NOT NULL,                                                         -- Category Names
    Description TEXT,                                                                   -- Category Long Descriptions
    IsActive INTEGER NOT NULL DEFAULT 1,                                                -- 1=True, 0=False
    DeletedAt TEXT DEFAULT NULL,
    DateCreated TEXT DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ParticipantID_FK) REFERENCES Participants(ParticipantID)
);

-- 📒 Transactions
CREATE TABLE Transactions (
    TransactionID INTEGER PRIMARY KEY AUTOINCREMENT,                        -- Transaction Identifier
    CategoryID_FK INTEGER NOT NULL,                                         -- Category Identifier (Foreign Key)
    InvoiceID_FK INTEGER,                                                   -- Invoice Identifier (Foreign Key)
    TransactionDate TEXT NOT NULL,                                          -- Date of the Transaction
    Amount NUMERIC NOT NULL,                                                -- Transaction Amount
    PaymentMethod TEXT,                                                     -- Method of Payment for the Transaction
    IsActive INTEGER NOT NULL DEFAULT 1,
    DeletedAt TEXT DEFAULT NULL,
    Notes TEXT,                                                             -- Contextual Notes about the Transaction
    DateCreated TEXT DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (CategoryID_FK) REFERENCES Categories(CategoryID),
    FOREIGN KEY (InvoiceID_FK) REFERENCES Invoices(InvoiceID)
);

-- 📝 Event Log
CREATE TABLE EventLog (
    EventID INTEGER PRIMARY KEY AUTOINCREMENT,                                                      -- Event Identifier
    UserID_FK INTEGER NOT NULL,                                                                     -- User Identifier (Foreign Key)
    EventDate TEXT NOT NULL,                                                                        -- Date of the Event
    EventType TEXT NOT NULL CHECK (EventType IN ('CREATE', 'READ', 'UPDATE', 'DELETE', 'ERROR')),   -- Type of Event
    TableName TEXT NOT NULL,                                                                        -- Table the event happened in
    RecordID INTEGER,                                                                               -- Record ID of the event happened to
    ErrorCode TEXT,                                                                                 -- Error Code of the event as a TEXT
    Description TEXT,                                                                               -- Description of the event
    DateCreated TEXT DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (UserID_FK) REFERENCES Users(UserID)
);

-- 👤 Users
CREATE TABLE Users (
    UserID INTEGER PRIMARY KEY AUTOINCREMENT,       -- User Identifier
    WindowsUsername TEXT UNIQUE NOT NULL,   -- System Username (Not sure this is required.  Was using this in Microsoft Access)
    FirstName TEXT,                         -- First Name of the User
    MiddleName TEXT,                        -- Middle Name of the User
    LastName TEXT,                          -- Last Name of the User
    Email TEXT,                             -- Email Address of the User
    IsActive INTEGER NOT NULL DEFAULT 1,            -- 1 for TRUE, 0 for FALSE
    DeletedAt TEXT DEFAULT NULL,
    DateCreated TEXT DEFAULT CURRENT_TIMESTAMP      -- SQLite uses TEXT for DATETIME and CURRENT_TIMESTAMP
);

-- 🎭 Roles (A static lookup table for all available roles)
CREATE TABLE Roles (
    RoleID INTEGER PRIMARY KEY AUTOINCREMENT,
    Role TEXT UNIQUE NOT NULL
);

-- 👤 User Roles (Join table linking Users to Roles)
CREATE TABLE UserRoles (
    UserID_FK INTEGER NOT NULL,
    RoleID_FK INTEGER NOT NULL,
    FOREIGN KEY (UserID_FK) REFERENCES Users(UserID),
    FOREIGN KEY (RoleID_FK) REFERENCES Roles(RoleID),
    UNIQUE (UserID_FK, RoleID_FK)
);

-- 👥 Participant Roles (Join table linking Participants to Roles)
CREATE TABLE ParticipantRoles (
    ParticipantID_FK INTEGER NOT NULL,
    RoleID_FK INTEGER NOT NULL,
    FOREIGN KEY (ParticipantID_FK) REFERENCES Participants(ParticipantID),
    FOREIGN KEY (RoleID_FK) REFERENCES Roles(RoleID),
    UNIQUE (ParticipantID_FK, RoleID_FK)
);

CREATE INDEX idx_categories_categoryname_participantid ON Categories (CategoryName, ParticipantID_FK);