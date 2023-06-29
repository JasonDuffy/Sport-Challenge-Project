# pull official base image
FROM node:alpine3.16

# set working directory
WORKDIR /app

# add `/app/node_modules/.bin` to $PATH
ENV PATH /app/node_modules/.bin:$PATH

# set timezone to Berlin
ENV TZ=Europe/Berlin

# install app dependencies
COPY package.json ./
COPY package-lock.json ./
RUN npm install --silent
RUN npm install react-scripts -g --silent

# add app
COPY . ./

# start app
CMD ["npm", "start"]


