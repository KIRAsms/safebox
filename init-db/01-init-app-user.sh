  #!/bin/bash
# ---------------------------------------------------------------------------
# Crée un utilisateur applicatif à droits limités (safebox_app).
# Ce script est exécuté automatiquement par l'image PostgreSQL, UNE SEULE FOIS,
# lors de la première initialisation du volume de données.
#
# L'application se connecte avec cet utilisateur au lieu du superutilisateur
# "postgres". safebox_app peut gérer ses propres tables (nécessaire pour
# Hibernate ddl-auto=update) mais NE PEUT PAS :
#   - supprimer la base de données
#   - créer ou modifier d'autres utilisateurs
#   - accéder à d'autres bases
#   - effectuer des actions de superutilisateur
# ---------------------------------------------------------------------------
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE USER safebox_app WITH PASSWORD '${APP_DB_PASSWORD}';
    GRANT CONNECT ON DATABASE $POSTGRES_DB TO safebox_app;
    GRANT USAGE, CREATE ON SCHEMA public TO safebox_app;
EOSQL
