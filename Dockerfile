FROM node:latest

WORKDIR /usr/src/app

COPY package*.json ./

RUN npm update && npm install

COPY . .

RUN chmod +x ./docker-entrypoint.sh

EXPOSE 8080

ENTRYPOINT ["node", "server.js"]



