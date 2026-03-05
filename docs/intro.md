# BarberFind API - Introdução

A BarberFind API é o backend central que atende:

- aplicativo mobile (React Native)
- painel web administrativo (React)

O backend foi construído em Spring Boot, com PostgreSQL e JWT, e disponibiliza operações para:

- autenticação e registro por perfil
- gestão de usuário
- gestão de barbearias
- catálogo de serviços
- gestão de vínculo entre barbearias e barbeiros
- agendamentos

## Perfis de acesso

- `CLIENT`
- `BARBER`
- `OWNER`

## Onde consultar os detalhes

- Arquitetura backend: `docs/architecture/backend.md`
- Banco de dados: `docs/architecture/database.md`
- Endpoints por domínio: `docs/api/*`
- Guias operacionais: `docs/guides/*`
