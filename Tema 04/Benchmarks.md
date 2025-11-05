## Fuentes reales y verificables sobre Virtual Threads vs WebFlux:

### Documentación oficial y presentaciones de Oracle:

1. **JEP 444 - Virtual Threads**
   - https://openjdk.org/jeps/444
   - Documentación oficial del proyecto Loom

2. **Presentaciones de Oracle sobre Project Loom:**
   - https://www.youtube.com/watch?v=lKSSBvRDmTg (Ron Pressler en Devoxx)
   - https://www.youtube.com/watch?v=lIq-x_iI-kc (Inside Java Podcast)

3. **Artículos técnicos de Spring:**
   - https://spring.io/blog/2022/10/11/embracing-virtual-threads
   - Blog oficial de Spring sobre adopción de Virtual Threads

### Benchmarks reales de la comunidad:

4. **Comparativa de pgjdbc (PostgreSQL JDBC):**
   - https://github.com/pgjdbc/pgjdbc/pull/2579
   - Tests reales con Virtual Threads vs thread pools tradicionales

5. **JMH Benchmarks de la comunidad:**
   - https://github.com/ebarlas/project-loom-comparison
   - Comparaciones con código reproducible

6. **Artículos técnicos con benchmarks:**
   - https://www.infoq.com/articles/java-virtual-threads/ (InfoQ)
   - https://foojay.io/today/java-21-virtual-threads-dude-wheres-my-lock/ (Foojay)