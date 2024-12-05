-- Disable foreign key checks temporarily
SET foreign_key_checks = 0;

-- Create the 'users' table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    is_active BIT,
    is_deleted BIT,
    password VARCHAR(255) NOT NULL,
    schema_name VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT UK_users_username_schema UNIQUE (username, schema_name),
    CONSTRAINT UK_users_schema_name UNIQUE (schema_name),
    CONSTRAINT UK_users_username UNIQUE (username)
) ENGINE=InnoDB;

-- Create the 'address' table
CREATE TABLE IF NOT EXISTS address (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_by VARCHAR(255) NOT NULL,
    created_date DATETIME(6) NOT NULL,
    last_modified_by VARCHAR(255),
    last_modified_date DATETIME(6),
    city VARCHAR(255),
    country VARCHAR(255),
    is_deleted BIT,
    state VARCHAR(255),
    street VARCHAR(255),
    zip_code VARCHAR(255),
    user_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT FK_address_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB;

-- Create the 'roles' table
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT NOT NULL AUTO_INCREMENT,
    is_deleted BIT,
    name VARCHAR(255),
    PRIMARY KEY (id),
    CONSTRAINT UK_roles_name UNIQUE (name)
) ENGINE=InnoDB;

-- Create the 'users_addresses' table
CREATE TABLE IF NOT EXISTS users_addresses (
    user_id BIGINT NOT NULL,
    addresses_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, addresses_id),
    CONSTRAINT FK_users_addresses_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT FK_users_addresses_address FOREIGN KEY (addresses_id) REFERENCES address (id),
    CONSTRAINT UK_users_addresses_address UNIQUE (addresses_id)
) ENGINE=InnoDB;

-- Create the 'users_roles' table
CREATE TABLE IF NOT EXISTS users_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT FK_users_roles_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT FK_users_roles_role FOREIGN KEY (role_id) REFERENCES roles (id),
    CONSTRAINT UK_users_roles_role UNIQUE (role_id)
) ENGINE=InnoDB;

-- Enable foreign key checks again
SET foreign_key_checks = 1;
