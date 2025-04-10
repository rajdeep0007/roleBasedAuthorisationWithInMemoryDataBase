Steps:

1) initially, if we are bringing in the dependency of spring security, the very first time we start the application,
we can see password getting generated in the console.
By default, Spring configures , a user whose name is "user" and password is what we find in the console and the role is also "ROLE_USER"
This configuration is saved in the in memory database
In order to ensure that any of the API is working finally, we need to pass a basic authorisation with the above credentials.

2) in order to override this user, we can create a bean of in memory database wherein we can pass her own user. Something like below.
  @Bean
     public InMemoryUserDetailsManager userDetailsService() {
         UserDetails adminUser = User.builder()
                 .username("admin")
                 .password(passwordEncoder().encode("admin123"))
                 .roles("ADMIN")
                 .build();
         UserDetails normalUser = User.builder()
                 .username("user")
                 .password(passwordEncoder().encode("user123"))
                 .roles("USER")
                 .build();
         return new InMemoryUserDetailsManager(adminUser,normalUser);
     }

NOTE: sending passing using  return new BCryptPasswordEncoder(); was giving 403 hence try using passwordEncoder().encode("user123")) with below bean
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}

NOTE: if you are not using JWT, then it is mandatory to enable http basic authentication like below
.httpBasic(Customizer.withDefaults())

3) if I need to ensure that there are some Api's which doesn't need to be a part of the spring security,
 I need to allow them in the spring security configuration as below
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    return httpSecurity
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(request ->
                    request.requestMatchers("/public/healthCheck", "/error").permitAll()
                            .requestMatchers("/admin/healthCheck").hasRole("ADMIN")
                            .anyRequest().authenticated())
            .httpBasic(Customizer.withDefaults())
            .build();
}

4) in case of incorrect authorisation we get something like below in the client
{
    "timestamp": "2025-04-06T19:39:35.242+00:00",
    "status": 403,
    "error": "Forbidden",
    "path": "/admin/healthCheck"
}

In order to have better response, we need to implement the below

@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

@Override
public void handle(HttpServletRequest request,
                   HttpServletResponse response,
                   AccessDeniedException accessDeniedException) throws IOException {
    response.setContentType("application/json");
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    response.getWriter().write("{ \"error\": \"Forbidden\", \"message\": \"Access denied\" }");
}

In order to activate this, we need to add the configuration and the security config file

.exceptionHandling(e -> e
                    .authenticationEntryPoint(authEntryPoint)
                    .accessDeniedHandler(accessDeniedHandler)
            )

Now the repsonse will look somethign like below
{
    "error": "Forbidden",
    "message": "Access denied"
}

5) similarly, in case of incorrect credentials when we get 401 we can have better response BUT
Spring Security's default httpBasic() interfering
If you're using JWT or a custom auth mechanism, calling .httpBasic() may override or bypass your
AuthenticationEntryPoint and hence we might not get better response