# Security Implementation Overview

This application implements a security model using Spring Security with JAAS integration and JWT for authentication. User accounts are stored in an XML file.

## Security Model

### Privileges

The system implements the following privileges:

1. `VIEW_VIDEO`: Allows users to view videos
2. `CREATE_COMPLAINT`: Allows users to submit complaints
3. `CREATE_VIDEO`: Allows users to upload new videos
4. `REVIEW_VIDEO`: Allows users to review and moderate videos

### Roles

The system implements the following roles, each with its own set of privileges:

1. `UNAUTHENTICATED`: No privileges
2. `USER`: Has privileges `VIEW_VIDEO`, `CREATE_COMPLAINT`, `CREATE_VIDEO`
3. `ADMIN`: Has all privileges: `VIEW_VIDEO`, `CREATE_COMPLAINT`, `CREATE_VIDEO`, `REVIEW_VIDEO`

## Implementation Details

### User Storage

User accounts are stored in an XML file (`users.xml`) at the root of the application. This file is automatically created when the application starts if it doesn't exist.

The XML structure looks like:

```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<users>
    <user>
        <username>admin</username>
        <password>[encrypted password]</password>
        <role>ADMIN</role>
        <enabled>true</enabled>
    </user>
    <user>
        <username>user</username>
        <password>[encrypted password]</password>
        <role>USER</role>
        <enabled>true</enabled>
    </user>
</users>
```

### Authentication Flow

1. The client sends credentials to `/api/auth/login`
2. The server authenticates using JAAS and XML-based user storage
3. If authentication is successful, a JWT token is returned to the client
4. The client includes this token in subsequent requests in the `Authorization` header
5. The JWT filter validates the token and populates the security context

### JAAS Integration

The application integrates JAAS with Spring Security:

1. A custom JAAS login module (`XmlUserLoginModule`) authenticates users against the XML file
2. Principals are created for users, roles, and privileges
3. The principals are converted to Spring Security authorities

### Default Users

The application creates two default users on startup if they don't exist:

1. Admin user:
   - Username: `admin`
   - Password: `admin`
   - Role: `ADMIN`

2. Regular user:
   - Username: `user`
   - Password: `user`
   - Role: `USER`

## API Security

The application secures various endpoints based on roles and privileges:

- Public endpoints (`/api/auth/**`, `/swagger-ui/**`, etc.) are accessible to all users
- Admin endpoints (`/api/admin/**`) require the `ADMIN` role
- Video review endpoints (`/api/video/review/**`) require the `REVIEW_VIDEO` privilege
- Video creation endpoints (`/api/video/create/**`) require the `CREATE_VIDEO` privilege
- Video viewing endpoints (`/api/video/view/**`) require the `VIEW_VIDEO` privilege
- Complaint creation endpoints (`/api/complaint/**`) require the `CREATE_COMPLAINT` privilege 
