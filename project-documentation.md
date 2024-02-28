# JHipster Project Documentation

Providing a high-level documentation of the technologies used in the JHipster project, and cheatsheets for how to use some of the most important technologies.

---

<!-- TOC -->

- [JHipster Project Documentation](#jhipster-project-documentation)
  - [Technologies/Libraries](#technologieslibraries)
    - [JHipster](#jhipster)
    - [JDL - Defining Entities](#jdl---defining-entities)
    - [JDL - Defining Relationships](#jdl---defining-relationships)
    - [Spring Boot (Back End Server)](#spring-boot-back-end-server)
      - [Database Layer: JPA (`src/main/java/team/bham/repository/`)](#database-layer-jpa-srcmainjavateambhamrepository)
      - [Domain Layer: Entity Models (`src/main/java/team/bham/domain/`)](#domain-layer-entity-models-srcmainjavateambhamdomain)
      - [Service Layer (`src/main/java/team/bham/service/`)](#service-layer-srcmainjavateambhamservice)
      - [Control Layer: REST Controllers (`src/main/java/team/bham/web/rest/`)](#control-layer-rest-controllers-srcmainjavateambhamwebrest)
      - [Security](#security)
    - [Liquibase](#liquibase)
    - [Maven](#maven)
    - [Node.js](#nodejs)
    - [NPM](#npm)
    - [Angular](#angular)
    - [Angular Components](#angular-components)
    - [Angular Router](#angular-router)
    - [Angular CLI (Command Line Interface)](#angular-cli-command-line-interface)
    - [Webpack](#webpack)
    - [SASS](#sass)
    - [Bootstrap](#bootstrap)
    - [Prettier](#prettier)
    - [Lint Staged](#lint-staged)
    - [Husky](#husky)
    - [ESLint](#eslint)
    - [Checkstyle](#checkstyle)
    - [Jest](#jest)
    - [GitLab CI/CD (Continuous Integration, Continuous Deployment)](#gitlab-cicd-continuous-integration-continuous-deployment)
  - [File & Directory Documentation](#file--directory-documentation)
    - [Top Level Files](#top-level-files)
    - [Folders](#folders)
  - [NPM Scripts](#npm-scripts)
  - [Cheat Sheets](#cheat-sheets)
  _ [Cheatsheet: JDL - Generating Entities (Database Tables)](#cheatsheet-jdl---generating-entities-database-tables)
  _ [Cheatsheet: Generating components & routes (Angular CLI)](#cheatsheet-generating-components--routes-angular-cli) \* [Cheatsheet: Adding NPM (JavaScript/TypeScript) Packages](#cheatsheet-adding-npm-javascripttypescript-packages)
  <!-- TOC -->

---

## Technologies/Libraries

### JHipster

https://www.jhipster.tech/

JHipster is a tool used to easily generate and develop full-stack web applications with a number of commonly used frameworks.

The JHipster Generator offers developers a number of different technology options for each layer of the application. (e.g. front-end framework: `Angular`, `React`, or `Vue`)

The full list of settings used when generating the JHipster application can be found in `README.md`, but a few crucial ones are listed below:

- `...`
- `? Which *production* database would you like to use? PostgreSQL `
- `? Would you like to use Maven or Gradle for building the backend? Maven `
- `? Which *Framework* would you like to use for the client? Angular `
- `...`

---

### JDL - Defining Entities

We can define an entity in a JDL file such as **Author** by declaring the following

```jdl
application {
  entities Author
}

entity Author {
  name String required
  age Integer min(0)
  profile_image ImageBlob unique
}
```

**Fields**

- Each entity can have any number of fields/properties e.g. `name`, `age`

**Field Types**

- Each **field** must have an associated **type** e.g. `String`, `Integer`

**Field Validations**

- Each **field** _may_ also have field validations e.g. `required`, `unique`, `min(0)`. Some of these validations are specific to the **type**
- **See: [JHipster field types and validations](https://www.jhipster.tech/jdl/entities-fields#field-types-and-validations)**
  > **To view full documentation on defining JDL entities, including RegEx validation and comments, see: https://www.jhipster.tech/jdl/entities-fields**

> See: [Cheatsheet: Creating an entity with JHipster](#cheatsheet-jdl---generating-entities-database-tables)

---

### JDL - Defining Relationships

We can define **relationships** in a JDL file between any two entities

Given we have our `Author` entity created in **JDL Entities Cheatsheet**, and we additionally define an entity named `Book`:

```jdl
entity Book {
  name String required
  coverImage ImageBlob required
}
```

We can define a bidirectional one-to-many relationship between `Author` and `Book` with the following:

```jdl
entity Author
entity Book

relationship OneToMany {
  Author{book} to Book{author required}
}
```

By **naming** the relationship on **both sides** (see `{book}` and `{author required}`) we create a **bidirectional** relationship, where each entity can access the other entity (or _entities_) that it's related to.

**E.g.,**

- Within the generated `Author` Java Class, we can access the `Books` it's related to using the `.getBooks()` method.
- Likewise, within the generated `Book` Java Class, we can access the `Author` it's related to using `.getAuthor()`

_Note: because we've declared the `Book -> Author` relationship to be `required`, a Book entity cannot be created without being related to an `Author` entity._

> **To view full documentation on defining JDL relationships, see https://www.jhipster.tech/jdl/relationships**

---

### Spring Boot (Back End Server)

> **Spring Boot's configuration for it's build, test and deployment stages is located in `src/main/resources/config/`**

> **The Spring Boot application is launched from the entrypoint located in `src/main/java/team/bham/TeamprojectApp.java`**

> **The Spring Boot masterclass can be found here: https://canvas.bham.ac.uk/courses/72989/files/16275125?module_item_id=3582179**

Spring Boot provides an easy way to create extendable and modular back-end Java applications, and allows for the easy integration of databases in full-stack applications.

Spring Boot is split into hierarchical layers: **Database, Domain, Service, and Control:**

<br>

#### Database Layer: JPA (`src/main/java/team/bham/repository/`)

Spring Boot provides **CRUD integration with databases** using the JPA (Java Persistence API), which is a specification for managing relational data in Java applications.

- The JPA is configured in `src/main/resources/config/application-dev.yml` and `src/main/resources/config/application-prod.yml` to provide integration with the **H2 database** in development, and the **Postgres** database in production
- > The **JPA integrations** for each entity (class decorator `@Repository`) are located in `src/main/java/team/bham/repository/`

<br>

#### Domain Layer: Entity Models (`src/main/java/team/bham/domain/`)

Spring Boot provides a Java class, or 'model', for **each entity** (Representing the data stored in the database). Each class has fields & methods corresponding to the database table’s columns. These methods use the [Database Layer](#database-layer-jpa-srcmainjavateambhamrepository) mentioned above to perform necessary CRUD operations.

- > The **Entity** models (class decorator `@Entity`) are located in `src/main/java/team/bham/domain/`

<br>

#### Service Layer (`src/main/java/team/bham/service/`)

Spring Boot provides a service layer for **implementing business logic** such as creating or updating a user. (e.g. `src/main/java/team/bham/service/UserService.java`)

- The main purpose of the service layer is to offer services to the [Control Layer](#control-layer-rest-controllers-srcmainjavateambhamwebrest).
- The Service Layer makes calls to the [Domain Layer](#domain-layer-entity-models-srcmainjavateambhamdomain) to access the data needed to implement the logic.
- > The application's **Service classes** (class decorator `@Service`) are located in `src/main/java/team/bham/service/`

<br>

#### Control Layer: REST Controllers (`src/main/java/team/bham/web/rest/`)

Spring Boot provides a number of classes to implement a **REST API** using the `@RestController` decorator. (e.g. `src/main/java/team/bham/web/rest/UserResource.java` defines the API endpoints for accessing data on the `User` entity)

- > The application's **REST controllers** (class decorator `@RestController`) are located in `src/main/java/team/bham/web/rest/`

<br>

#### Security

Spring Boot also implements **authentication** and **authorisation** (**access-control**). Authentication is predominantly handled for you, but authentication is handled in `src/main/java/team/bham/security/SecurityUtils.java`

- > The application's **Security Protocols** are located in `src/main/java/team/bham/security/`
- > To modify the security constraints on API endpoints directly, edit `src/main/java/team/bham/config/SecurityConfiguration.java`
- Read more about spring security: https://spring.io/guides/topicals/spring-security-architecture

<br>

---

### Liquibase

> **Liquibase is configured in `src/main/resources/config/liquibase/`**

Liquibase is used to keep track of changes to database structures, for example, when you use JDL to generate or update an entity (see [JDL Cheatsheet](#cheatsheet-jdl---generating-entities-database-tables))

JHipster uses Liquibase to manage its database updates, and stores its configuration and changesets in `src/main/resources/config/liquibase/`

---

### Maven

> **Maven is configured in `pom.xml`**
>
> _See [File & Directory Documentation](#file--directory-documentation)_

https://maven.apache.org/index.html

Maven is a **Java** project management and comprehension tool. Based on the concept of a project object model (POM), Maven can manage a project's build, deployment and documentation from a central piece of information.

Maven is used the JHipster project primarily to compile, test and run the back-end [SpringBoot Server](#spring-boot-back-end-server) application along with managing Java Dependencies.

- When running Maven (`./mvnw`) with no additional arguments, the default goal `spring-boot:run` is used.
  - This goal starts both the back-end spring boot application, along with the static front-end (for hot-reloading of the front-end, use `npm start` to start the front-end only)

Maven manages:

- Java dependencies (much like [NPM](#npm) does for JavaScript dependencies)
- Build/Compilation processes
- Development & Production profiles

---

### Node.js

**Node.js** is a JavaScript runtime environment which allows JavaScript code to be run server-side rather than being limited to the browser, which it was originally developed for.

Node is required to be installed in a project to run JavaScript or TypeScript code. In this project, it's used in the background by the [Angular CLI](#angular-cli-command-line-interface) to serve the front-end webapp.

---

### NPM

> **NPM is configured in `package.json`**
>
> _See [File & Directory Documentation](#file--directory-documentation)_
>
> - _IMPORTANT: To add npm packages, use the command-line tool `npm` rather than editing the `package.json` directly._
> - **_See: [Cheatsheet: Adding NPM Packages](#cheatsheet-adding-npm-javascripttypescript-packages)_**

**npm** is a package manager for **JavaScript** and **TypeScript**.

The `npm` client commandline tool allows for the installation and import of JS/TS packages in a project.

It also allows packages to be version controlled using semantic versioning (`v.X.X.X`), so that installed packages remain compatible with the development environment and other packages.

When packages are installed using `npm install [package-name]`, the `npm` client searches for the package from the [npm registry](https://www.npmjs.com/).
If the package is found, it's recorded as a **dependency** in `package.json`, and the package's code is installed locally into `./node_modules/[package-name]`

If a package is altered/deleted from `node_modules`, the missing dependency can be easily reinstalled using `npm install`, which updates, fixes or installs all of the listed dependencies listed in `package.json`

- _This is why the `node_modules` folder is not tracked by Git, but `package.json` is, as `node_modules` would add unnecessary and possibly outdated library code to the repository. When the dependencies are needed, they can all be cleanly installed using `npm install`_

---

### Angular

> **Angular is configured in `angular.json`**
>
> _See [File & Directory Documentation](#file--directory-documentation)_

https://angular.io/

Angular is a front-end web development framework, built on TypeScript, used for creating efficient and sophisticated single-page web apps.

Angular is a **component-based framework** for building scalable and modular web applications.

- The Angular html, code and styling for the Angular components can be found in separate folders within `/src/main/webapp/app/`
- To learn more about components, see [Angular Component](#angular-components)

> To create a new Angular Component, see the [Cheatsheet: Generating Components & Routes](#cheatsheet-generating-components--routes-angular-cli) on generating new components with the Angular CLI

> **JHipster provides specific documentation on how Angular is utilised within JHipster Applications:**
>
> **https://www.jhipster.tech/using-angular/**

---

### Angular Components

Recommended Reading: https://angular.io/guide/what-is-angular#components

An **Angular component** can be identified by the `.component.ts` suffix (e.g. `my-custom-name.component.ts`) and contains the following:

1. A **decorator** to define configuration options for things like:

   - A **selector** that defines what the tag name is when referring a component in a template
   - An **HTML template** that controls what is rendered to the browser
   - An **[SCSS](#sass) file** that controls the styling for the HTML template

     e.g.

     ```
     @Component({                            // Decorator
      selector: 'jhi-home',                  // Tag Selector
      templateUrl: './home.component.html',  // HTML Template
      styleUrls: ['./home.component.scss'],  // SCSS Styling File
     })
     ```

2. A **TypeScript class** that defines the behavior of the component. Examples include handling user input, managing state, defining methods, etc.

   e.g.

   ```
   export class HomeComponent implements OnInit, OnDestroy {   // TypeScript Class
    /* Component behavior is defined in here */
   }
   ```

> **The recommended way to create new Angular components is with the Angular CLI**
>
> _See: [Cheatsheet: Generating Components & Routes](#cheatsheet-generating-components--routes-angular-cli)_

---

### Angular Router

> **Angular Routes are configured in `src/main/webapp/app/app-routing.module.ts`**

The Angular router is used to configure multiple pages (or 'routes') in the Angular single page application (SPA). Each one of these routes renders a different [Angular Component](#angular-components) to the browser, **without** serving a different webpage, hence 'single page' application.

_Note: Routes defined within the Angular router can also redirect to other **'subrouters'**, to implement nested pages, as we will see below_

<br>

**The parent Angular routing module can be found at: `src/main/webapp/app/app-routing.module.ts`, and defines the following routes:**

[//]: # '* `/`'

- `/login` renders the component at `/src/main/webapp/app/login/login.module`
- `/account` renders the component at `/src/main/webapp/app/account/account.module`
- `/admin` redirects to the **subrouter** at `/src/main/webapp/app/admin/admin-routing.module.ts`

  **...this subrouter renders a number of 'subroutes' at `/admin/*`:**

  - `/admin/user-management` renders `src/main/webapp/app/admin/user-management/user-management.module`
  - `/admin/docs` renders `src/main/webapp/app/admin/docs/docs.module`
  - `/admin/configuration` renders `src/main/webapp/app/admin/configuration/configuration.module`
  - `/admin/health` renders `src/main/webapp/app/admin/health/health.module`
  - `/admin/logs` renders `src/main/webapp/app/admin/logs/logs.module`
  - `/admin/metrics` renders `src/main/webapp/app/admin/metrics/metrics.module`

  _Note: There are other routes that are automatically generated for error pages, and to provide pages for each JHipster entity_

> If you want to create a new route, you will need to direct it to an Angular module, which can be created by generating a new [Angular Component](#angular-components)
>
> If you also wish to have your new route appear in the **navbar**, you will need to add a button to `src/main/webapp/app/layouts/navbar/navbar.component.html`

---

### Angular CLI (Command Line Interface)

> `ng` is the Angular command-line-interface tool

The Angular CLI is used to generate, update, maintain, build, and run the front-end Angular application in the project.

The front-end Angular application can be started standalone using `./npmw start`, which in turn runs `ng serve -hmr`

- This runs the Angluar CLI front-end webapp with hot module replacement (the page refreshes the content as you code)

**The Angular CLI can also be used to generate new [Angular Components](#angular-components):**

> _See: [Angular CLI Cheatsheet: Generating New Components](#cheatsheet-generating-components--routes-angular-cli)_

---

### Webpack

Webpack is a module bundler for front-end web applications. It takes code from TypeScript (`.ts`), SASS (`.scss`) and Angular template (`.html`) files (among others) and packages them into files that're renderable in the browser (`.js`, `.css`, plain `.html`)

_Webpack is used by **Angular** (using the `@angular-builders/custom-webpack` package) to package both the development and production front-end._

_It has very few direct configuration files, as it's mostly configured and handled by the [Angular CLI](#angular-cli-command-line-interface). The few configuration files available are located in `/webpack/`_

_It's also responsible for serving the **hot-reloadable** front-end used for development, started with `npm start`_

---

### SASS

https://sass-lang.com/

SASS _(described as CSS with superpowers)_ is an extension of CSS which adds a number of useful features to CSS styling, such as variables, nested rules, mixins, functions, and more.

> IMPORATANT: Often when you wish to format HTML in the front-end [Angular components](#angular-components), it's better to use [Bootstrap](#bootstrap) rather than raw SCSS, as it provides a framework for building **responsive** UIs, which cannot be guaranteed if you use raw SCSS.

SASS files (`.scss`) are used to provide styling for the [Angular Components](#angular-components) found in the project.

For example, the stylings for the `Navbar` component can be found in `src/main/webapp/app/layouts/navbar/navbar.component.scss`

The **Global** SCSS stylings for the front-end webapp can be found in `src/main/webapp/content/scss/`

---

### Bootstrap

https://getbootstrap.com/

Bootstrap is a HTML styling library built with [SASS](#sass), with in-built and easy to use grid systems and components.

To use bootstrap, we apply **predefined classes** to HTML elements in our angular templates. See the following example from ``:

```angular2html
<div class="row">
    <div class="col-md-3">
        <span class="hipster img-fluid rounded"></span>
    </div>

    <div class="col-md-9">
        <h1 class="display-4"><span>Welcome, Java Hipster!</span> (Teamproject)</h1>

        <p class="lead">This is your homepage</p>
        ...
```

As you can see, there are a number of classes applied to the elements in the above angular template, such as:

- `row` Defines the div as a bootstrap grid
- `col-md-3` Defines the div to be a column (`col`) within the grid. And, for the medium (`md`) viewpoint breakpoint, to take up `3` columns (of the available 12)
- `img-fluid rounded` which allows the image to be responsive to the parent element's size (`img-fluid`) and makes the corners rounded (`rounded`)
- `display-4` Formats the `<p>` tag with the fourth largest heading

> **Many of the bootstrap classes are intuitive with some basic understanding, but for full documentation, visit:**
>
> **https://getbootstrap.com/docs/5.3/**
>
> The Bootstrap documentation allows you to search for any given type of formatting or class to find out it's usages.

---

### Prettier

> **Prettier is configured in `.prettierrc` and `.prettierignore`**
>
> _See [File & Directory Documentation](#file--directory-documentation)_

Prettier is used to enforce consistent formatting (e.g indentation, brackets, etc.) across a project. It removes all original styling and ensures outputted code conforms to a consistent style.

In our project, three prettier libraries are installed: `prettier`, `prettier-plugin-java` and `prettier-plugin-packagejson`.

These libraries can be used to check or reformat files in:

- `./`, `./src` `./webpack/`, and `./.blueprint/`

On file types:

- `.md`, `.json`, `.yml`, `.html`, `.cjs`, `.mjs`, `.js`, `.ts`, `.tsx`, `.css`, `.scss`, `.java`

**To perform a manual prettier check or re-format on the whole project, use:**

- `npm run prettier:check`
- `npm run prettier:format`

**Important: It's encouraged to run a `prettier:check` before a `prettier:format` so you're aware of which files may be changed.**

> _Note: Prettier is automatically run by [Lint Staged](#lint-staged) during the `pre-commit` Git Hook (configured by [Husky](#husky))_

---

### Lint Staged

> Lint Staged is configured in `.lintstagedrc.js`
>
> _See [File & Directory Documentation](#file--directory-documentation)_

Lint Staged is a package which runs a **linter or formatter** (e.g. [Prettier](#prettier)) on files that are staged for commit rather than on the whole project (which is often time consuming).

> The Lint Staged script is triggered by the `pre-commit` Git hook (configured by [Huksy](#husky))
>
> - Lint Staged is configured to run [Prettier](#prettier) on files staged for commit, as defined in `.lintstagedrc.js`

---

### Husky

> **Husky is configured in `./husky/`**
>
> _See [File & Directory Documentation](#file--directory-documentation)_

Husky is used to automatically create git hooks that are able to run arbitrary scripts at a number of points during the git lifecycle.

Git is configured to look in the `./.husky/` directory (`core.hookspath=.husky`) for Git Hook scripts.

- For example: before each commit, the hook `./.husky/pre-commit` is executed
  - When this hook is fired, [Lint Staged](#lint-staged) is run, which in turn runs [Prettier](#prettier) to enforce consistent styling on the files about to be committed.

---

### ESLint

> **ESLint is configured in `.eslintrc.json` and `.eslintignore`**
>
> _See [File & Directory Documentation](#file--directory-documentation)_

ESLint is a linter (code error checker) used to check JavaScript and TypeScript files (`.js` and `.ts`) for errors.

The rules which it checks against are defined in `.eslintrc.json`

Also in this file is an `extends` property, which contains a list of other linter rules for ESLint to include in the ruleset.

```
...
  "extends": [
    "eslint:recommended",
    "plugin:@typescript-eslint/recommended",
    "plugin:@typescript-eslint/recommended-requiring-type-checking",
    "plugin:@angular-eslint/recommended",
    "prettier",
    "eslint-config-prettier"
  ],
...
```

_ESLint includes `plugin:@angular-eslint/recommended` so that ESLint can check the front-end Angular code for errors_
_ESLint includes `prettier` and `eslint-config-prettier` in order to disable ESLint rules that would conflict with prettier's more opinionated styling rules._

ESLint can be run on all `.js` and `.ts` files using:

- `npm run lint`

> _Note: ESLint doesn't appear to be automatically run during any of the build steps or [GitLab CI/CD](#gitlab-cicd-continuous-integration-continuous-deployment) processes_

---

### Checkstyle

> **Checkstyle is configured in `checkstyle.xml`**
>
> _See [File & Directory Documentation](#file--directory-documentation)_

Checkstyle is a Java library used to check for consistent formatting in a project's Java code and generate a report.

The rules and files for Checkstyle to check against are configured in `checkstyle.xml`

> Checkstyle is automatically run as part of the maven build process, as defined here in `pom.xml`
>
> ```
> ...
> <configuration>
>     <configLocation>checkstyle.xml</configLocation>
>     <includes>pom.xml,README.md</includes>
>     <excludes>.git/**/*,target/**/*,node_modules/**/*,node/**/*</excludes>
>     <sourceDirectories>./</sourceDirectories>
> </configuration>
> <executions>
>     <execution>
>         <goals>
>             <goal>check</goal>
>         </goals>
>     </execution>
> </executions>
> ...
> ```

---

### Jest

> **Jest is configured in `jest.conf.js`**
>
> _See [File & Directory Documentation](#file--directory-documentation)_

Jest is a **JavaScript/TypeScript** testing framework which performs unit and integration tests on JS/TS files.

The configuration file defines which the locations and files to run the tests on. In our project, jest will look for tests with the file extension `spec.ts` to run in `/src/main/webapp/app/`

> Jest is automatically run during the build process of the default [Maven](#maven) build process using `ng test` (Angular CI, configured in `angular.json`)

To run jest **directly** with a coverage report:

- `npm run jest`

Jest generates an output report in `/target/test-results/TESTS-results-jest.xml` and a sonar-formatted version of the same report in `/target/test-results/jest/TESTS-results-sonar.xml`

---

### GitLab CI/CD (Continuous Integration, Continuous Deployment)

> **GitLab CI/CD is configured in `.gitlab-ci.yml`**
>
> _See [File & Directory Documentation](#file--directory-documentation)_

GitLab CI & CD is a method of software development where code is continuously monitored for changes, built, tested, and deployed.

This reduces the chance of buggy code as small changes can be pinpointed as the root of issues.

The CI & CD steps for this project are configured in `.gitlab-ci.yml`

Full Documentation: https://docs.gitlab.com/ee/ci/index.html

---

## File & Directory Documentation

---

### Top Level Files

| File Name                | Technology/Library                                                        | Category                       | Affects    | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| ------------------------ | ------------------------------------------------------------------------- | ------------------------------ | ---------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| README.md                |                                                                           |                                |            | Basic project instructions/tutorial as generated by JHipster                                                                                                                                                                                                                                                                                                                                                                                                      |
| .yo-rc.json              | Yeoman                                                                    | Project Management             |            | **Yeoman Generator Configuration**<br/>yeoman is used to generate a jhipster project from the command line.                                                                                                                                                                                                                                                                                                                                                       |
| .yo-rc-global.json       | Yeoman                                                                    | Project Management             |            | **Yeoman Generator Configuration**<br/>Defines a global jhipster package name `team.bham` for yeoman-created jhipster project generations                                                                                                                                                                                                                                                                                                                         |
| .gitignore               | Git                                                                       | Project Management             |            | **Git Configuration**<br/>The `.gitignore` configuration specifies which untracked files to intentionally ignore when staging files for commit. Common examples include library files (e.g. node_modules) which are installed during the build process, serving no use to include in the repository                                                                                                                                                               |
| .gitattributes           | Git                                                                       | Project Management             |            | **Git Configuration**<br/>The `.gitattributes` configuration associates file extensions (e.g. `.json`, `.png`) with certain file **types** such as `text` or `binary`. These associated types alter how Git operations handle these file types; when using `git add` and `git commit`, files may be standardised with consistent end-of-line (eol) characters as specified in `.gitattributes`.<br/> _Full documentation: https://git-scm.com/docs/gitattributes_ |
| .gitlab-ci.yml           | [GitLab CI/CD](#gitlab-cicd-continuous-integration-continuous-deployment) | Project Management             |            | **GitLab CI/CD Configuration**<br/>Defines the configuration for the CI/CD Jobs. Includes the stages of CI/CD amd the scripts to be run at each of these stages.                                                                                                                                                                                                                                                                                                  |
| pom.xml                  | [Maven](#maven)                                                           | Deployment                     | Full Stack | **Maven Project Configuration File**<br/>https://maven.apache.org/pom.html<br/>- Maven "Project Object Model"<br/>- The XML format is a tag-based language siilar to HTML<br/>- The `pom.xml` contains all necessary information about a project, as well as configurations of plugins to be used during the build process                                                                                                                                        |
| mvnw                     | [Maven](#maven)                                                           | Command Line Tool              | Full Stack | **Maven Wrapper**<br/>- mvn wrapper allowing to use a specific Maven version for the project (use in place of `mvn` commands)<br/>- runs `pom.xml`'s `defaultGoal` of `spring-boot:run`, which starts the SpringBoot Application (See [Maven](#maven))                                                                                                                                                                                                            |
| mvnw.cmd                 | [Maven](#maven)                                                           | Command Line Tool              | Front End  | `mvnw` packaged for Windows: see above `mvnw`                                                                                                                                                                                                                                                                                                                                                                                                                     |
| npmw                     | [NPM](#npm)                                                               | Command Line Tool              | Front End  | **NPM (Node Package Manager) Wrapper**<br/>- npm wrapper allowing to use a specific NPM and Node version for the project (use in place of `npm` commands)                                                                                                                                                                                                                                                                                                         |
| npmw.cmd                 | [NPM](#npm)                                                               | Command Line Tool              | Front End  | `npmw` packaged for Windows: see above `npmw`                                                                                                                                                                                                                                                                                                                                                                                                                     |
| package.json             | [NPM](#npm)                                                               | Development, Build, Deployment | Full-Stack | **NPM Project Configuration**<br/>`package.json` is used by NPM (see above) to define which libraries (also called packages or dependencies) are required by the project, along with [scripts for development, testing, and production](#npm-scripts).<br/>One such script is `./npmw start` which runs the front-end Angular webapp                                                                                                                              |
| package-lock.json        | [NPM](#npm)                                                               | Build, Deployment              | Front-End  | `package-lock.json` specifies the exact tree of dependencies that should be installed in the project. This differs from package.json as it specifies **exact** versions, along with any sub-dependencies for each dependency.                                                                                                                                                                                                                                     |
| angular.json             | [Angular](#angular)                                                       | Development, Build, Deployment | Front End  | **Angular Configuration**<br/>Configurations for the [Angular CLI](#angular-cli-command-line-interface) and Angular build steps.<br/>Full documentation can be found at https://angular.io/guide/workspace-config                                                                                                                                                                                                                                                 |
| ngsw-config.json         | [Angular CLI](#angular-cli-command-line-interface)                        | Development                    | Front End  | **Angular Service Worker Configuration**<br/>Configuration that specifies which files and URLs should be cached by the Angular service worker. This is processed by the [Angular CLI](#angular-cli-command-line-interface)                                                                                                                                                                                                                                        |
| jest.conf.js             | [Jest](#jest)                                                             | Testing                        | Front End  | **Jest Configuration**<br/> [Jest](#jest)'s configuration for front-end angular unit tests                                                                                                                                                                                                                                                                                                                                                                        |
| sonar-project.properties | SonarQube                                                                 | Testing                        | Full Stack | **SonarQube Configuration**<br/>Sonar can be used to analyse code quality. The Sonar Instance can be run alone using `docker-compose`<br/>Full documentation can be found at https://www.jhipster.tech/code-quality/                                                                                                                                                                                                                                              |
| .prettierrc              | [Prettier](#prettier)                                                     | Formatting                     | Full Stack | **`prettier` Configuration File**<br/>Configuration for the [Prettier](#prettier) formatting tool.<br/>Defines the rules that should be enforced across the files in the project.                                                                                                                                                                                                                                                                                 |
| .prettierignore          | [Prettier](#prettier)                                                     | Formatting                     | Full Stack | **`prettier` Configuration File**<br/>Defines files that the [Prettier](#prettier) library should ignore                                                                                                                                                                                                                                                                                                                                                          |
| checkstyle.xml           | Checkstyle                                                                | Formatting                     | Back End   | **Checkstyle Configuration**<br/>The `.checkstyle.xml` file is used to define what files and by what standards Checkstyle should check Java code for                                                                                                                                                                                                                                                                                                              |
| .lintstagedrc.js         | [Lint-Staged](#lint-staged)                                               | Linting                        | Full Stack | **`lint-staged` Configuration File**<br/>lint-staged is a `npm` library used to run a linter (code error checker) specifically on files staged for commit. This is run when the `pre-commit` git hook is fired. The git hooks are configured by **Husky**                                                                                                                                                                                                         |
| .eslintrc.json           | [ESLint](#eslint)                                                         | Linting                        | Front End  | **ESLint Configuration**<br/>Configures the rules used by [ESLint](#eslint) to check for errors in front-end code.                                                                                                                                                                                                                                                                                                                                                |
| .eslintignore            | [ESLint](#eslint)                                                         | Linting                        | Front End  | **ESLint Configuration**<br/>Configures which files and directories should be ignored by [ESLint](#eslint)'s error checking (e.g. dependency/library code)                                                                                                                                                                                                                                                                                                        |
| tsconfig.json            | TypeScript                                                                | Compilation                    | Front End  | **Front-End Compiler Options**<br/>- Specifies the common front-end code (TypeScript and Angular) compiler options<br/>- Also defines the entrypoint of the webapp at `src/main/webapp/`                                                                                                                                                                                                                                                                          |
| tsconfig.app.json        | TypeScript                                                                | Compilation                    | Front End  | Extends tsconfig.json with webapp specific overrides                                                                                                                                                                                                                                                                                                                                                                                                              |
| tsconfig.spec.json       | TypeScript                                                                | Compilation                    | Front End  | Extends tsconfig.json with webapp testing (jest) specific overrides                                                                                                                                                                                                                                                                                                                                                                                               |
| .browserslistrc          | Browserslist                                                              | Compilation                    | Front End  | **Browerslist Configuration**<br/>Browserslist is used to define targets for web-browser compatability. This is used by linters/compilers (e.g. ESLint) so that css and js can be compiled into an application that works as intended on all browsers, regardless of their current css or js support. E.g. A compiler will be able to compile styles written in CSS4 to be CSS3 compatable.                                                                       |
| .editorconfig            | EditorConfig                                                              | IDE                            | N/A        | **EditorConfig Configuration**<br/>helps developers define and maintain consistent coding styles between different editors and IDEs. Defines things like end of line charset, indentation settings, etc. [editorconfig.org](editorconfig.org)                                                                                                                                                                                                                     |

### Folders

| Folder Name                          | Technology      | Usage                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |     |
| ------------------------------------ | --------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | --- |
| .husky/                              | [Husky](#husky) | Location of [Husky](#husky-unused) Wrapper (`./husky.sh`) and git hook scripts (e.g. `pre-commit`)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |     |
| target/                              |                 | Location of compiled/built code and code test reports                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |     |
| node_modules/                        | NPM             | Location of [Node](#npm) modules                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |     |
| .devcontainer/                       | VSCode          | Generated by jhipster to enable an easy setup of a VS Code environment for development. Contains configuration of NPM version, VS Code extensions, et.c                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |     |
| webpack/                             | Webpack         | Contains configuration for webpack in development using ESLint and BrowserSync, and for production with BundleAnalyzer                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |     |
| .mvn/                                | Maven           | Contains configuration for [Maven](#maven) JVM configuration (i.e. Java flags to be run when executing the maven project).<br/>Also contains `maven-wrapper.jar` which allows the maven wrapper to be downloaded in projects that prohibit checking in binary data.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |     |
| .jhipster/                           | JHipster        | Stores persistent data about the JHipster configuration & entities                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |     |
| .git/                                | Git             | Stores data pertaining to the git version control including branches, commits, hooks, etc.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |     |
| src/test/                            |                 | Unit Test Sources                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |     |
| src/test/java/                       | Java            | Java Unit Test sources                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |     |
| src/main/docker/                     | Jib             | Location of automatically generated Docker containters to run the project out of.<br/>Containers are generated using maven package `jib-maven-plugin`.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |     |
| src/main/resources/                  |                 | Application/Library resources                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |     |
| src/main/resources/config/           | Spring Boot     | [Spring Boot](#spring-boot-back-end-server) configuration files                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |     |
| src/main/resources/config/tls/       |                 | Contains the TLS keystore                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |     |
| src/main/resources/config/liquibase/ | Liquibase       | [Liquibase](#liquibase) configuration and database changesets                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |     |
| src/main/resources/templates/        |                 | Contains HTML templates for email messages (e.g. Account activation & password reset emails)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |     |
| src/main/resources/i18n/             | i18n            | Configuration for language internationalisation support.<br/>See https://www.jhipster.tech/installing-new-languages/                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |     |
| src/main/webapp/                     | Angular         | <h3>Angular Web App</h3> <br/>Web application sources<br/> JHipster generates the angular web application using Angular CLI. This integration is done by generating an `angular.json` file<br/>The Angular application is using `typescript` rather than `javascript`<br/><h4>Project Structure from JHipster Docs:</h4><pre>webapp<br/><br>├── app - Your application<br>│ ├── account - User account management UI<br>│ ├── admin - Administration UI<br>│ ├── config - Some utilities files<br>│ ├── core - Common building blocks like configuration and interceptors<br>│ ├── entities - Generated entities (more information below)<br>│ ├── home - Home page<br>│ ├── layouts - Common page layouts like navigation bar and error pages<br>│ ├── main - Main page<br>│ ├── main.component.ts - Main application class<br>│ ├── login - Login page<br>│ ├── shared - Common services like authentication and internationalization<br>│ ├── app.module.ts - Application modules configuration<br>│ ├── app-routing.module.ts - Main application router<br>├── content - Static content<br>│ ├── css - CSS stylesheets<br>│ ├── images - Images<br>│ ├── scss - Sass style sheet files will be here if you choose the option<br>├── i18n - Translation files<br>├── swagger-ui - Swagger UI front-end<br>├── 404.html - 404 page<br>├── favicon.ico - Fav icon<br>├── index.html - Index page<br>├── robots.txt - Configuration for bots and Web crawlers<br></pre> <br/><h4>For each entity generated, the following front-end files will be available:</h4> <pre>webapp<br/>├── app<br/>│ ├── entities<br/>│ ├── foo - CRUD front-end for the Foo entity<br/>│ ├── foo.component.html - HTML view for the list page<br/>│ ├── foo.component.ts - Controller for the list page<br/>│ ├── foo.model.ts - Model representing the Foo entity<br/>│ ├── foo.module.ts - Angular module for the Foo entity<br/>│ ├── foo.route.ts - Angular Router configuration<br/>│ ├── foo.service.ts - Service which access the Foo REST resource<br/>│ ├── foo-delete-dialog.component.html - HTML view for deleting a Foo<br/>│ ├── foo-delete-dialog.component.ts - Controller for deleting a Foo<br/>│ ├── foo-detail.component.html - HTML view for displaying a Foo<br/>│ ├── foo-detail.component.ts - Controller or displaying a Foo<br/>│ ├── foo-dialog.component.html - HTML view for editing a Foo<br/>│ ├── foo-dialog.component.ts - Controller for editing a Foo<br/>│ ├── foo-popup.service.ts - Service for handling the create/update dialog pop-up<br/>│ ├── index.ts - Barrel for exporting everything<br/>├── i18n - Translation files<br/>│ ├── en - English translations<br/>│ │ ├── foo.json - English translation of Foo name, fields, ...<br/>│ ├── fr - French translations<br/>│ │ ├── foo.json - French translation of Foo name, fields, ...</pre> |     |
| src/main/webapp/app/core/            | Angular         | Contains some common data structures used within the Angular Web App                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |     |
| src/main/webapp/WEB-INF/             |                 | Contains project metadata                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |     |
| src/main/java/team/bham/config/      | Spring Boot     | Configuration files generated by Spring Boot                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |     |
| src/main/java/team/bham/repository/  | Spring Boot     | Java files for the [Spring Boot **Database Layer**](#database-layer-jpa-srcmainjavateambhamrepository)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |     |
| src/main/java/team/bham/domain/      | Spring Boot     | Java files for the [Spring Boot **Domain Layer (Entity Models)**](#domain-layer-entity-models-srcmainjavateambhamdomain)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |     |
| src/main/java/team/bham/service/     | Spring Boot     | Java files for the [Spring Boot **Service Layer**](#service-layer-srcmainjavateambhamservice)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |     |
| src/main/java/team/bham/web/rest/    | Spring Boot     | Java files for the [Spring Boot **Control Layer (REST Controllers)**](#control-layer-rest-controllers-srcmainjavateambhamwebrest)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |     |
| src/main/java/team/bham/security/    | Spring Boot     | Java files for [Spring Boot **Security Protocols**](#security)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |     |

## NPM Scripts

There are a number of scripts found in `package.json` which perform a number of operations involved in testing, building, and deployment.

Very few of these will need to be run manually, but a few are listed below, taken from the JHipster Documentation.

From JHipster:

```
Some npm commands used as an example.

npm run lint: check for code style issues in the TypeScript code
npm run lint:fix: try to automatically correct TypeScript lint issues
npm run tsc: compile the TypeScript code
npm run test: run unit tests with Jest
npm run test:watch: keep the Jest unit tests running, for live feedback when code is changed
npm run e2e: run “end to end” tests with Protractor (only works if the Protractor option has been selected when the project was generated)
```

## Cheat Sheets

---

### Cheatsheet: JDL - Generating Entities (Database Tables)

To generate a JHipster entity (analogous to a database **table**) we can use a `.jdl` file (JHipster Domain Language)

**JDL Documentation: [Defining Entities](#jdl---defining-entities) and [Defining Relationships](#jdl---defining-relationships)**

> **Generating the Entity**
>
> Once a .jdl file has been created, you can run `jhipster jdl [file].jdl --ignore-application` to generate all the necessary files and database tables

<details>
<summary style="font-weight: bold; font-style: italic">Generating Entities: Extra Information</summary>

> To create an JHipster entity (analogous to a database **table**), the following is needed:
>
> - A database table
> - A Liquibase change set
> - A JPA Entity
> - A Spring Data JPA Repository
> - A Spring MVC REST Controller, which has the basic CRUD operations
> - An Angular router, a component and a service
> - An HTML view
> - Integration tests, to validate everything works as expected
> - Performance tests, to see if everything works smoothly
>
> _And, if you have several entities, you will likely want to have relationships between them. For this, you will need:_
>
> - _A database foreign key_
> - _Specific JavaScript and HTML code for managing this relationship_
>
> **Luckily, this can all be generated for us using JHipster by using a `.jdl` file (JHipster Domain Language)**

</details>

---

### Cheatsheet: Generating components & routes (Angular CLI)

> **IMPORTANT: Generating a new component should _not_ be confused with [Generating an Entity](#cheatsheet-jdl---generating-entities-database-tables). Although generating a JHipster entity generates Angular components, the reverse is not true. Generating a new component should be used _exclusively_ for new pages/routes or reusable components.**

The Angular CLI can be used to extend the angular application. The main Angular CLI command is `ng`

JHipster Angular CLI documentation: https://www.jhipster.tech/using-angular/#using-angular-cli

<br>

> ### Generating a new Page/Route
>
> To create a new page, we need to create an **Angular Module and [Angular Component](#angular-components)**.
>
> - An **Angular Module** is necessary so that the router at `src/main/webapp/app/app-routing.module.ts` knows which components to include and render for a given module.
>
> **To generate an Angular Module and Angular Component named `my-component`, we can use the command:**
>
> `ng generate module my-component --module=app --route=component-route`
>
> **Command Breakdown**
>
> - `ng generate` uses the [Angular CLI](#angular-cli-command-line-interface) blueprint generator
> - `module my-component` tells the Angular CLI to generate a **new module** named `my-component`
> - `--module=app --`
>
> _Where `my-component` is the name of the component, and `component-route` is the path you wish to find your new page at_
>
> ---
>
> **This will generate the files:**
>
> - create `src/main/webapp/app/my-component/my-component.component.html`
> - create `src/main/webapp/app/my-component/my-component.component.scss`
> - create `src/main/webapp/app/my-component/my-component.component.spec.ts`
> - create `src/main/webapp/app/my-component/my-component.component.ts`
> - create `src/main/webapp/app/my-component/my-component.module.ts`
> - create `src/main/webapp/app/my-component/my-component-routing.module.ts`
> - update `src/main/webapp/app/app-routing.module.ts`

<br>

> ### Generating standalone Components
>
> The Angular CLI can be used to generate a new component using the `ng generate component [component-name]` command. E.g. for a new component named `my-component`:
>
> `ng generate component my-component`
>
> **This will generate a few files:**
>
> - create `src/main/webapp/app/my-component/my-component.component.html`
> - create `src/main/webapp/app/my-component/my-component.component.ts`
> - update `src/main/webapp/app/app.module.ts`

The Angular Module generated for this component can then be used in the [Angular Router](#angular-router) to create a new page

---

### Cheatsheet: Adding NPM (JavaScript/TypeScript) Packages

To install an NPM package to be used in the front-end, such as [ngx-charts](https://www.npmjs.com/package/@swimlane/ngx-charts), use `npm install @swimlane/ngx-charts`

You can then use an import at the top of a `.ts` file to use the library such as `import { NgxChartsModule } from '@swimlane/ngx-charts';`

---
