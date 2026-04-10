#!/usr/bin/env bash

set -euo pipefail

SEED_FILES=(
  "db/seeds/01_seed_clientes.sql"
  "db/seeds/02_seed_cuentas.sql"
  "db/seeds/03_seed_usuarios_auth.sql"
)

usage() {
  cat <<'EOF'
Uso:
  ./seed_remote_db.sh <database_url>
  DATABASE_URL=<database_url> ./seed_remote_db.sh
  ./seed_remote_db.sh --status <database_url>

Notas:
  - Este script asume que Flyway ya creo el esquema.
  - Si la URL no incluye sslmode, se agrega sslmode=require.
EOF
}

DATABASE_URL_VALUE="${DATABASE_URL:-}"
STATUS_ONLY="false"

while (($# > 0)); do
  case "$1" in
    --status)
      STATUS_ONLY="true"
      shift
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      if [[ -n "$DATABASE_URL_VALUE" ]]; then
        echo "Error: solo se permite una database URL." >&2
        usage >&2
        exit 1
      fi
      DATABASE_URL_VALUE="$1"
      shift
      ;;
  esac
done

if [[ -z "$DATABASE_URL_VALUE" ]]; then
  echo "Error: debes pasar la database URL." >&2
  usage >&2
  exit 1
fi

if [[ "$DATABASE_URL_VALUE" != *"sslmode="* ]]; then
  if [[ "$DATABASE_URL_VALUE" == *\?* ]]; then
    DATABASE_URL_VALUE="${DATABASE_URL_VALUE}&sslmode=require"
  else
    DATABASE_URL_VALUE="${DATABASE_URL_VALUE}?sslmode=require"
  fi
fi

run_psql() {
  psql "$DATABASE_URL_VALUE" -v ON_ERROR_STOP=1 "$@"
}

assert_schema_exists() {
  local relation
  relation="$(run_psql -tAc "select to_regclass('public.cliente');")"
  if [[ "$relation" != "cliente" ]]; then
    echo "Error: la tabla public.cliente no existe." >&2
    echo "Primero despliega o arranca la app para que Flyway cree el esquema." >&2
    exit 1
  fi
}

print_status() {
  echo ""
  echo "Estado actual:"
  run_psql -c "select 'cliente' as tabla, count(*) as total from cliente
union all
select 'cuenta', count(*) from cuenta
union all
select 'usuario', count(*) from usuario
union all
select 'usuario_rol', count(*) from usuario_rol
order by tabla;"

  echo ""
  echo "Migraciones registradas por Flyway:"
  run_psql -c "select version, description, success
from flyway_schema_history
order by installed_rank;"
}

assert_schema_exists

if [[ "$STATUS_ONLY" == "true" ]]; then
  print_status
  exit 0
fi

for seed_file in "${SEED_FILES[@]}"; do
  if [[ ! -f "$seed_file" ]]; then
    echo "Error: no existe $seed_file" >&2
    exit 1
  fi

  echo "Ejecutando $seed_file ..."
  run_psql -f "$seed_file"
done

print_status
