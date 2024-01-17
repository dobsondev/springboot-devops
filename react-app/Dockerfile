FROM node:21.5.0
WORKDIR /home/node/react-app
COPY ./react-app/package.json .
RUN npm install --quiet
COPY ./react-app/. .
EXPOSE 3000
CMD ["npm", "run", "build"]