Note :
1) since we are clearing the DB , so please ensure to create a user else it will throw 401 with Bad credentials.
( this is in TO DO )
2) the name of the project has jwtdemo , but this is also in progress

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

-----------------------------------------------------------------------------------------------------------------------
USING CUSTOMUSERDETAILSERVICE INSTEAD OF INMEMORYDB
-----------------------------------------------------------------------------------------------------------------------

1) replace InMemoryDB with your customuserdetailservice by using the below code

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .role(user.getRole())
                .build();
    }

2)we need to enable or plug this to our security config for which we need a bean of DaoAuthenticationProvider

@Bean
public DaoAuthenticationProvider daoAuthenticationProvider(CustomUserDetailsService userDetailsService) {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(userDetailsService);
    provider.setPasswordEncoder(passwordEncoder());
    return provider;
}

3)learn about the concept of @EventLister using which we are able to delete all data from the table on startup

@Component
public class StartupDataCleaner {

    private final UserRepository userRepository;

    public StartupDataCleaner(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void clearTableOnStartup() {
        userRepository.deleteAll();
        System.out.println("✅ Table cleared at application startup.");
    }
}

4) the request payload to create Admin user is
{
    "username":"Rajdeep",
    "password":"rajdeep",
    "email":"rajdeep@gmail.com",
    "role":"ROLE_ADMIN"
}