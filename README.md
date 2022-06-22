# Spring Boot REST API Starter Project with JWT

### Features
* Authorities - with Global Method Security `Secured`,`JSR250`,`PrePost`
* Roles - User has many roles and role has many authorities (Authorities)
* Automatic Permission (Authorities) sync to database
* User Management
* Automatic Initial User initialization
* JWT with Refresh token support
* Multi-Factor Authentication Support (SMS,TOTP,EMAIL)
* Email, Phone Verification
* Password Reset
* Swagger UI
* Lombok 


#### Authorities Usage Example
Checkout rest endpoints for more
```
@GetMapping("/{id}")
@PostAuthorize("hasAuthority('" + Authorities.READ_USER + "') or returnObject.body.username == principal.username")
public ResponseEntity<UserDTO> getUser(@PathVariable("id") final Long id) {
  return ResponseEntity.ok(userService.get(id));
}
```
