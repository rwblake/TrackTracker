# TrackTracker
## Track your Spotify listening habits with your friends!
TrackTracker is a Spotify listening analysis platform which provides statistics and insights for the songs
that you and your friends have listened to. Its core functionality is split into 6 sections: the feed, personal
insights, friend stats, playlist analyser, profile page, and friend manager.

### This system was created by University of Birmingham students as part of the Team Project module
Its members (in alphabetical order of surname):

- Reuben B (rxb208@student.bham.ac.uk)
- Robert B (rxb256@student.bham.ac.uk)
- Joseph H (jfh245@student.bham.ac.uk)
- Shoaib H (sxh1499@student.bham.ac.uk)
- Matthew H (mxh1131@student.bham.ac.uk)
- Frederick R (fxr233@student.bham.ac.uk)
- Christopher W (cxw298@student.bham.ac.uk)

## Home Feed
![](/example_images/home_feed.jpg)
The Home Feed is the first page to greet a user any time they open the app after logging on. It gives them
one place to see all the recent updates of their friends, whilst also acting as a clean launchpad to access
the different features of the app.
A shortcut for your profile is shown at the top alongside your pinned friends. This page’s main feature,
however, is the Feed: a collection of insights and updates personalised to each user. Imagine 

Instagram’s feed but solely fuelled by you and your friend’s listening activity on Spotify: the longer you
use the app, and listen to Spotify, the more the feed grows. You can like cards to access them later on,
and use the filters provided on the side to focus your feed on different types of card, such as friend
requests and playlists.

## My Insights
![](/example_images/my_insights.jpg)
My Insights can be accessed from the main navigation bar found at the top of the page, as long as a user
is logged in with a valid Spotify account.
After all of the user’s accumulated data has been fetched, they’re presented with a number of interactive
charts and tables containing statistics.
• The Top Genres, Top Albums, and Top Artists charts displays their most frequently listened to
genres, albums and artists respectively
• 4 leaderboards rank their ‘top 3’ in various different categories such as favourite artist or genre.
These leaderboards act as a summary of what the user likes the most.
The page, by default, shows statistics from within the past week. This can be changed using the time
period picker at the top of the page. This allows them to choose between Week, Month, Year and All Time
– when a user clicks one of these time periods, the charts and tables update appropriately.

## Playlist Analyser
![](/example_image/playlist_analyser.jpg)
The Playlist Analyser allows a user to view insights about a given playlist. It’s usable without an account,
although being logged in enables TrackTracker to access a user’s private playlists.
The user enters or pastes a link to a Spotify playlist. The user is asked to wait as processing takes place.
Then, upon success, a number of graphs and charts are shown. These are, from top-to-bottom, left-toright:
• Averages of various audio properties of the playlist’s songs
• Notable songs from the playlist derived from statistical analysis on its songs’ audio features.
These are clickable buttons which link to the referenced songs on Spotify
• Pie charts showcasing the most frequently occurring artists and genres in the playlist. The top 3
are displayed in a table below the chart - hovering over or tabbing through the chart reveals the
others.
• A bar chart which maps the playlist’s songs against their time of release. By default, this is
displayed by decade, but this can be toggled to individual years.
A logged-in user can also quickly re-access the stats for recently viewed playlists at the bottom of the
page.

## Friend Activity
Friends Activity can be accessed from the main navigation bar found at the top of the page, as long as a
user is logged in with a valid Spotify account. When it’s opened, there’s a short delay while relevant data
is retrieved and aggregated, indicated by the Calculating… heading. Once the data has been retrieved,
the page displays two main sections for the insights.
• The first of these sections is Popular with your friends, where the top tracks, artists and albums
amongst your friends are displayed in a responsive carousel-style card stack.
• The second is Your Friends Leaderboards, where you can see where you rank amongst your
friends in several categories: Most Hours Streamed, and Most Artists Listened To.
As with My Insights, the page, by default, shows statistics from within the past week, but this can be
changed using the time period picker in the top right.

## Profile
![](/example_images/profile.jpg)
Each user has a profile, based on the information they supplied upon registration, and their Spotify
Account. The page links to their associated Spotify Account.
Clicking Edit Profile allows the user to add a bio to the page, alter privacy settings, and change their
profile picture. By default, the profile picture is lifted from the user’s Spotify account. Clicking App
Settings allows the user to alter the account’s associated name email address. The playlist cards, when
clicked, take you to the Playlist Analyser page for the chosen playlist.
When the profile icon is clicked, a list of different options is displayed in a dropdown menu

## Friend Manager
The Friends page allows a user to manage connections with their friends. It’s only available if you have
signed into an account.
On loading, the user is presented with a list of their current friends, alongside their profile pictures and
the song and album that they have most recently listened to. Clicking on the song or album title sends the
user to the relevant Spotify page so that they can play the song too. The app provides friend
recommendations based on the genres of songs that users have listened to so that you can see how
similar your music taste is to others.Easily send friend requests with the plus button. Active friend
requests are displayed with the option to accept or reject them. The search bar allows you to search for
new users, send them friend requests, or block users by name.

# Creation

This application was generated using JHipster 7.9.4, you can find documentation and help at [https://www.jhipster.tech/documentation-archive/v7.9.4](https://www.jhipster.tech/documentation-archive/v7.9.4).

- [Setting up the project for the first time](#installing-the-development-environment)
- [Creating a new project to reflect this one](#creating-a-new-project)
- [Project Structure](#project-structure)
- [Development](#development)
- [Building](#building-for-production)
- [Testing](#testing)
- [Optional Extras](#optional-extras)
- [Research Folder](#research-folder)

_For more in-depth details and breakdown of the project, see [project-documentation.md](project-documentation.md)._

## Setting up the project for development

After cloning from GitLab, you need to do a few things before you can effectively edit and run your code.

### Installing the development environment

The app requires `Java JDK`, `maven`, `npm`, and `node`.
The project was generated with the following versions in mind:

`% java --version openjdk 11.0.17 2022-10-18`

`% mvn -version Apache Maven 3.9.4`

`% npm -version 8.19.3`

`% node --version v18.13.0`

JHipster v7.9.4 maintenance is installed using NPM:
`npm install -g jhipster/generator-jhipster#v7.x_maintenance`

### Information about databases in project

There are two databases used in the project: one for production (PostgreSQL), and one for development (H2).

You could attempt to set up the H2 dev or PostgreSQL databases in your IDE,
although this sometimes causes issues with running the code.

If you do wish to (not suggested):

- The details required for creating a new data source in IntelliJ IDEA are given in `src/main/resources/config/application-dev.yml`.
- It is currently (since last checked):

```yml
datasource:
type: com.zaxxer.hikari.HikariDataSource
url: jdbc:h2:file:./target/h2db/db/teamproject;DB_CLOSE_DELAY=-1;MODE=LEGACY
username: teamproject
password:
hikari:
  poolName: Hikari
  auto-commit: false
```

_The production database parameters often get picked up by the IDE, but they don't work if you set them up._

## Creating a new project

You can set up your own project using JHipster that will reflect this one.
To do so, follow the below options and modifications.

### JHipster Settings

When running JHipster to generate a project for the first time, you will be asked a set of questions.
Here are the settings used for this project:

- `? Which *type* of application would you like to create? Monolithic application (recommended for simple projects) `
- `? What is the base name of your application? teamproject `
- `? Do you want to make it reactive with Spring WebFlux? No `
- `? What is your default Java package name? team.bham `
- `? Which *type* of authentication would you like to use? JWT authentication (stateless, with a token) `
- `? Which *type* of database would you like to use? SQL (H2, PostgreSQL, MySQL, MariaDB, Oracle, MSSQL) `
- `? Which *production* database would you like to use? PostgreSQL `
- `? Which *development* database would you like to use? H2 with disk-based persistence `
- `? Which cache do you want to use? (Spring cache abstraction) Ehcache (local cache, for a single node) `
- `? Do you want to use Hibernate 2nd level cache? Yes `
- `? Would you like to use Maven or Gradle for building the backend? Maven `
- `? Do you want to use the JHipster Registry to configure, monitor and scale your application? No `
- `? Which other technologies would you like to use? `
- `? Which *Framework* would you like to use for the client? Angular `
- `? Do you want to generate the admin UI? Yes ? Would you like to use a Bootswatch theme (https://bootswatch.com/)? Flatly `
- `? Choose a Bootswatch variant navbar theme (https://bootswatch.com/)? Light `
- `? Would you like to enable internationalization support? No `
- `? Please choose the native language of the application English `
- `? Besides JUnit and Jest, which testing frameworks would you like to use? `
- `? Would you like to install other generators from the JHipster Marketplace? No`

### Further modifications required

_Modified by Madasar Shah for the Team Project._

- `.yo-rc.json` contains the setting used to generate this app (see below)

- `pom.xml` line 87 changed to `<jib-maven-plugin.architecture>arm64</jib-maven-plugin.architecture>` this depends on the arch you will deploy the docker image to n.b. use `amd64` for Intel or AMD virtual machines

- `src/main/webapp/app/layouts/footer/footer.component.html` modified to add the text `You are accessing an experimental web application developed by participants of the Team Project 2024 module.`

- `.gitlab-ci.yml` generated using command `% jhipster ci-cd` , generate GitLab CI, and use docker to build.

- `.gitlab-ci.yml` modified to remove unused test sections, `maven-package` section edited to fix bug with arm version of docker image used to build as chromium was missing :/

- `.gitlab-ci.yml` added `publish-docker:` stage using internal git.cs.bham.ac.uk docker image registry

- `src/main/docker/` updated `app.yml`, added `Caddyfile` `caddy.yml` for web serving, added `prd.yml` for production deployment+database, added `install-app.sh` and `install-docker.sh` for app deployment over ssh

- `.gitlab-ci.yml` added `deploy-dev:` to deploy app to cloud virtual machine using CI variables `$USER`@`IP` and `$RSA` set in the repo `Setting -> CI/CD -> variables` n.b. for non university VMs this requires DNS entries pointing to `$IP` for e.g. `dev.${CI_PROJECT_NAME}.bham.team` to work

- `.gitlab-ci.yml` added `deploy-prod:` to deploy production app to cloud virtual machine n.b. for non university VMs this requires DNS entries pointing to `$IP` e.g. `{CI_PROJECT_NAME}.bham.team` to work

## Project Structure

Node is required for generation and recommended for development. `package.json` is always generated for a better development experience with prettier, commit hooks, scripts and so on.

In the project root, JHipster generates configuration files for tools like git, prettier, eslint, husky, and others that are well known and you can find references in the web.

`/src/*` structure follows default Java structure.

- `.yo-rc.json` - Yeoman configuration file
  JHipster configuration is stored in this file at `generator-jhipster` key. You may find `generator-jhipster-*` for specific blueprints configuration.
- `.yo-resolve` (optional) - Yeoman conflict resolver
  Allows to use a specific action when conflicts are found skipping prompts for files that matches a pattern. Each line should match `[pattern] [action]` with pattern been a [Minimatch](https://github.com/isaacs/minimatch#minimatch) pattern and action been one of skip (default if ommited) or force. Lines starting with `#` are considered comments and are ignored.
- `.jhipster/*.json` - JHipster entity configuration files

- `npmw` - wrapper to use locally installed npm.
  JHipster installs Node and npm locally using the build tool by default. This wrapper makes sure npm is installed locally and uses it avoiding some differences different versions can cause. By using `./npmw` instead of the traditional `npm` you can configure a Node-less environment to develop or test your application.
- `/src/main/docker` - Docker configurations for the application and services that the application depends on

## Development

Before you can build this project, you must install and configure the following dependencies on your machine:

1. [Node.js][]: We use Node to run a development web server and build the project.
   Depending on your system, you can install Node either from source or as a pre-packaged bundle.

After installing Node, you should be able to run the following command to install development tools.
You will only need to run this command when dependencies change in [package.json](package.json).

```
npm install
```

We use npm scripts and [Angular CLI][] with [Webpack][] as our build system.

Run the following commands in two separate terminals to create a blissful development experience where your browser
auto-refreshes when files change on your hard drive.

```
./mvnw
npm start
```

Npm is also used to manage CSS and JavaScript dependencies used in this application. You can upgrade dependencies by
specifying a newer version in [package.json](package.json). You can also run `npm update` and `npm install` to manage dependencies.
Add the `help` flag on any command to see how you can use it. For example, `npm help update`.

The `npm run` command will list all of the scripts available to run for this project.

### PWA Support

JHipster ships with PWA (Progressive Web App) support, and it's turned off by default. One of the main components of a PWA is a service worker.

The service worker initialization code is disabled by default. To enable it, uncomment the following code in `src/main/webapp/app/app.module.ts`:

```typescript
ServiceWorkerModule.register('ngsw-worker.js', { enabled: false }),
```

### Managing dependencies

For example, to add [Leaflet][] library as a runtime dependency of your application, you would run following command:

```
npm install --save --save-exact leaflet
```

To benefit from TypeScript type definitions from [DefinitelyTyped][] repository in development, you would run following command:

```
npm install --save-dev --save-exact @types/leaflet
```

Then you would import the JS and CSS files specified in library's installation instructions so that [Webpack][] knows about them:
Edit [src/main/webapp/app/app.module.ts](src/main/webapp/app/app.module.ts) file:

```
import 'leaflet/dist/leaflet.js';
```

Edit [src/main/webapp/content/scss/vendor.scss](src/main/webapp/content/scss/vendor.scss) file:

```
@import '~leaflet/dist/leaflet.css';
```

Note: There are still a few other things remaining to do for Leaflet that we won't detail here.

For further instructions on how to develop with JHipster, have a look at [Using JHipster in development][].

### Using Angular CLI

You can also use [Angular CLI][] to generate some custom client code.

For example, the following command:

```
ng generate component my-component
```

will generate few files:

```
create src/main/webapp/app/my-component/my-component.component.html
create src/main/webapp/app/my-component/my-component.component.ts
update src/main/webapp/app/app.module.ts
```

### JHipster Control Center

JHipster Control Center can help you manage and control your application(s). You can start a local control center server (accessible on http://localhost:7419) with:

```
docker-compose -f src/main/docker/jhipster-control-center.yml up
```

## Building for production

### Packaging as jar

To build the final jar and optimize the teamproject application for production, run:

```
./mvnw -Pprod clean verify
```

This will concatenate and minify the client CSS and JavaScript files. It will also modify `index.html` so it references these new files.
To ensure everything worked, run:

```
java -jar target/*.jar
```

Then navigate to [http://localhost:8080](http://localhost:8080) in your browser.

Refer to [Using JHipster in production][] for more details.

### Packaging as war

To package your application as a war in order to deploy it to an application server, run:

```
./mvnw -Pprod,war clean verify
```

## Testing

To launch your application's tests, run:

```
./mvnw verify
```

### Client tests

Unit tests are run by [Jest][]. They're located in [src/test/javascript/](src/test/javascript/) and can be run with:

```
npm test
```

For more information, refer to the [Running tests page][].

### Code quality

Sonar is used to analyse code quality. You can start a local Sonar server (accessible on http://localhost:9001) with:

```
docker-compose -f src/main/docker/sonar.yml up -d
```

Note: we have turned off authentication in [src/main/docker/sonar.yml](src/main/docker/sonar.yml) for out of the box experience while trying out SonarQube, for real use cases turn it back on.

You can run a Sonar analysis with using the [sonar-scanner](https://docs.sonarqube.org/display/SCAN/Analyzing+with+SonarQube+Scanner) or by using the maven plugin.

Then, run a Sonar analysis:

```
./mvnw -Pprod clean verify sonar:sonar
```

If you need to re-run the Sonar phase, please be sure to specify at least the `initialize` phase since Sonar properties are loaded from the sonar-project.properties file.

```
./mvnw initialize sonar:sonar
```

For more information, refer to the [Code quality page][].

## Optional Extras

You can enhance your development experience with the below modifications.

### Using Docker to simplify development (optional)

You can use Docker to improve your JHipster development experience. A number of docker-compose configuration are available in the [src/main/docker](src/main/docker) folder to launch required third party services.

For example, to start a postgresql database in a docker container, run:

```
docker-compose -f src/main/docker/postgresql.yml up -d
```

To stop it and remove the container, run:

```
docker-compose -f src/main/docker/postgresql.yml down
```

You can also fully dockerise your application and all the services that it depends on.
To achieve this, first build a docker image of your app by running:

```
npm run java:docker
```

Or build a arm64 docker image when using an arm64 processor os like MacOS with M1 processor family running:

```
npm run java:docker:arm64
```

Then run:

```
docker-compose -f src/main/docker/app.yml up -d
```

When running Docker Desktop on MacOS Big Sur or later, consider enabling experimental `Use the new Virtualization framework` for better processing performance ([disk access performance is worse](https://github.com/docker/roadmap/issues/7)).

For more information refer to [Using Docker and Docker-Compose][], this page also contains information on the docker-compose sub-generator (`jhipster docker-compose`), which is able to generate docker configurations for one or several JHipster applications.

### Continuous Integration (optional)

To configure CI for your project, run the ci-cd sub-generator (`jhipster ci-cd`), this will let you generate configuration files for a number of Continuous Integration systems. Consult the [Setting up Continuous Integration][] page for more information.

[jhipster homepage and latest documentation]: https://www.jhipster.tech
[jhipster 7.9.4 archive]: https://www.jhipster.tech/documentation-archive/v7.9.4
[using jhipster in development]: https://www.jhipster.tech/documentation-archive/v7.9.4/development/
[using docker and docker-compose]: https://www.jhipster.tech/documentation-archive/v7.9.4/docker-compose
[using jhipster in production]: https://www.jhipster.tech/documentation-archive/v7.9.4/production/
[running tests page]: https://www.jhipster.tech/documentation-archive/v7.9.4/running-tests/
[code quality page]: https://www.jhipster.tech/documentation-archive/v7.9.4/code-quality/
[setting up continuous integration]: https://www.jhipster.tech/documentation-archive/v7.9.4/setting-up-ci/
[node.js]: https://nodejs.org/
[npm]: https://www.npmjs.com/
[webpack]: https://webpack.github.io/
[browsersync]: https://www.browsersync.io/
[jest]: https://facebook.github.io/jest/
[leaflet]: https://leafletjs.com/
[definitelytyped]: https://definitelytyped.org/
[angular cli]: https://cli.angular.io/

## Research Folder

For the Machine Learning tech report, there is a manual implementation of K-Means Clustering.

See the source code here [`Research/KMeansClustering.java`](Research/KMeansClustering.java)
