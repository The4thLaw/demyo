FROM node:16-slim AS node-build
WORKDIR /node-app
COPY demyo-vue-frontend .
RUN npm install
RUN npm run build

FROM maven:3.9-eclipse-temurin-11 AS mvn-build
WORKDIR /mvn-app
COPY . .
# Generate m2 settings with a secret and access to the GitHub packages
RUN mkdir ~/.m2
ARG GITHUB_PKG_TOKEN
RUN echo "<settings><servers><server><id>github-the4thlaw</id><username>The4thLaw</username><password>$GITHUB_PKG_TOKEN</password></server></servers></settings>" > ~/.m2/settings.xml
# Clean to be sure
RUN mvn clean -Pprod,prod-win,vue
# Re-integrate the node build
COPY --from=node-build /node-app/dist/css demyo-vue-frontend/target/classes/public/css
COPY --from=node-build /node-app/dist/fonts demyo-vue-frontend/target/classes/public/fonts
COPY --from=node-build /node-app/dist/js demyo-vue-frontend/target/classes/public/js
# Run the build, including the pre-built node stuff
RUN mvn install -DskipTests -Pprod

FROM eclipse-temurin:11-jre-alpine
WORKDIR /demyo
# TODO: use a specific user to run demyo?
COPY --from=mvn-build /mvn-app/demyo-app/target/dependencies/* lib/
COPY --from=mvn-build  /mvn-app/demyo-app/target/demyo-app-*.jar lib/
COPY --from=mvn-build /mvn-app/demyo-web/target/demyo-web-*.war war/
COPY --from=mvn-build /mvn-app/demyo-app/target/legacy-h2-versions/* legacy-h2-versions/
RUN mkdir /demyo-data
CMD java -Xmx256m -Duser.home=/demyo-data -Ddemyo.http.address=0.0.0.0 -cp 'lib/*' org.demyo.desktop.Start

EXPOSE 8080

# Sample run command:
# docker run --name demyo -p 8080:8080 -v demyo-data:/demyo-data -d --rm demyo:3.1.0
