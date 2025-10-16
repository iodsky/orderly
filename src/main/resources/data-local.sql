-- Roles
INSERT IGNORE INTO roles (id, role)
VALUES
  ('1a3f911c-4555-4ba2-8cc4-46d062d4c4c2', 'ADMIN'),
  ('d17846b1-bdaf-400b-a0b4-d85fd5e4c453', 'CUSTOMER');

-- Users
INSERT IGNORE INTO users (id, email, first_name, last_name, username, password, role_id)
VALUES
  ('8a0b934c-1e51-4079-be33-6e3b40e23b3f',
   'admin@email.com',
   'Admin',
   'User',
   'admin',
   '$2a$12$a0vq73QmysMpLEBwBY2AouoeicWYBHOpknxUjYdkgWY9FAogpH4jW',
   '1a3f911c-4555-4ba2-8cc4-46d062d4c4c2');
