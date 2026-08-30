# Database migration notes

The migration chain is normalized for PostgreSQL and verified from V1 through
V19 by automated tests. Historical migrations V2-V17 previously contained
MySQL-style identity syntax and schema/name mismatches, so their checksums have
changed.

## New database

Run the application with the `prod` profile. Flyway creates the complete schema,
then Hibernate validates it without modifying it.

## Database that already has Flyway history

Do not run an upgrade blindly. First:

1. Take and test a database backup.
2. Compare the installed migration checksums and schema with this release.
3. Review the changed historical scripts and V18/V19 with the operator.
4. Run `flyway repair` only after that review confirms the old migrations were
   successfully applied and the checksum change is expected.
5. Run `flyway migrate`, start with `ddl-auto=validate`, and perform smoke tests.

`flyway repair` changes schema history metadata; it does not fix a partially
applied or structurally different database. Such databases need an explicit,
reviewed reconciliation migration.
