# Ecosistema de APIs (Auth, Produtos e Estoque) com JWT e Docker

Aproveitando o feriado prologando de carnaval para desenvolver mais um projeto 🚀
Esse projeto tem um ecossistema que é composto por três serviços: authAPI (autenticação/autorização com JWT, banco usuarios), produtos-api (catálogo de produtos e agregação, banco produto)
e estoque-api (fonte oficial de quantidades por SKU, banco estoque).
As APIs validam JWT emitido pela authAPI e a produtos-api integra-se à estoque-api via OpenFeign, propagando o header Authorization. A infraestrutura de bancos roda em Docker (PostgreSQL + pgAdmin) exposta no host (localhost:5433 para Postgres),
garantindo um ambiente de desenvolvimento padronizado, isolado e reprodutível.
