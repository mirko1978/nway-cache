Example: User id
================

The use case realized is an in-memory cache on an application server, storing data associated with user id, in order to 
avoid a database dip for every request.  

The code is dived in two parts a set of REST api for managing the user id and a web fronted that consume the API.

### REST API

The REST api is developed using Spring boot with Tomcat as application server, Hibernate as JPA implementation and 
H2 as the database (it is in memory).  

#### REST webservice specification:

<br/>
<table>
    <thead>
    <th>Method</th>
    <th>URL</th>
    <th>Input Body</th>
    <th>Return Body</th>
    <th>Description</th>
    </thead>
    <tbody>
    <tr>
        <td><code>GET</code></td>
        <td>/users/{id}</td>
        <td>Not required</td>
        <td>User JSON object</td>
        <td>Retrieve information about the user with {id}<br/>
            <code>HTTP 404</code> code returned if the {id} is not found
        </td>
    </tr>
    <tr>
        <td><code>GET</code></td>
        <td>/users</td>
        <td>Not required</td>
        <td>Array of user JSON objects</td>
        <td>Retrieve all the users</td>
    </tr>
    <tr>
        <td><code>POST</code></td>
        <td>/users</td>
        <td>User JSON object</td>
        <td>Nothing</td>
        <td>Create a new user</td>
    </tr>
    <tr>
        <td><code>POST</code></td>
        <td>/users/load</td>
        <td>Not required</td>
        <td>Nothing</td>
        <td>Load 50 users</td>
    </tr>
    <tr>
        <td><code>PUT</code></td>
        <td>/users/{id}</td>
        <td>User JSON object</td>
        <td>Nothing</td>
        <td>Update an existing user with {id}<br/>
            <code>HTTP 404</code> code returned if the {id} is not found
        </td>
    </tr>
    <tr>
        <td><code>DELETE</code></td>
        <td>/users/{id}</td>
        <td>Not required</td>
        <td>Nothing</td>
        <td>Delete an existing user with {id}<br/>
            <code>HTTP 404</code> code returned if the {id} is not found
        </td>
    </tr>
    </tbody>
</table>

#### REST API Structure

The API structure is divided in three layers: DAO, Service, Controller.

* **DAO**: Is implemented by Spring Data. The only necessary code is defining the DAO interface `UserDAO`
* **Service**: The N Way Cache is used on this layer in order to avoid unnecessary call to database. This layer is accessible 
via the `UserServices` interface. Its implementation is done via `CachedUserServices`.
* **Controller**: `RestController`is defining the endpoints for the REST api. It calls the `UserService` for delegating the API implementation.

`User` class describe the model of a user.  
`Application` class is the entry point for Spring boot in order to launch H2 in memory database and the tomcat application 
server.  
`NotFoundException` is raised when a user is not found for a id.


### WEB Frontend

The WEB frontend is an addons to the REST api in order to have an easy way to interrogate them.
 
The web fronted is developed using AngularJs and Bootstrap for the CSS.  
It is an implementation of a basic CRUD operation around a user.



