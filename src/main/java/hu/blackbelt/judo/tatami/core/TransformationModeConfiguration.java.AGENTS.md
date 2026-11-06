# `TransformationModeConfiguration.java`

OSGi (Karaf) source of the container-wide transformation mode.

- DS `@Component(immediate=true, service=self, configurationPid=TransformationMode.CONFIG_PID, configurationPolicy=OPTIONAL)`.
- `@Activate @Modified apply(Map<String,Object>)` reads `mode`; parses via `TransformationMode.parse`; warns on invalid non-blank value; calls `TransformationMode.setConfiguredDefault(mode or null)`; logs effective mode.
- `@Deactivate deactivate()` clears the configured default (back to ZETA unless property set).
- Karaf config: `etc/hu.blackbelt.judo.tatami.transformation.cfg` with `mode = ETL`. ETL needs feature `judo-platform-etl` (brings `-etl` bundles + Epsilon). Without cfg: ZETA.
- System property `judo.transformation.mode` still wins over cfg.
- Not used outside OSGi (plain Java/Spring: use the system property).

See change: split-etl-artifacts.
