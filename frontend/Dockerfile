FROM node:alpine as build

WORKDIR /app

COPY package*.json /app/
RUN mkdir node_modules
RUN npm ci
COPY . .
EXPOSE 9090
CMD ["npm", "run", "dev"]