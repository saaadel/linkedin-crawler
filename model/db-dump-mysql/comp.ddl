CREATE TABLE companies
(
  id       INT                     NOT NULL AUTO_INCREMENT
    PRIMARY KEY,
  link     TEXT                    NULL,
  name     TEXT                    NULL,
  business TEXT                    NULL,
  location TEXT                    NULL,
  size     TEXT                    NULL,
  about    TEXT                    NULL,
  website  TEXT                    NULL,
  checked  SMALLINT(1) DEFAULT '0' NOT NULL
);

