# Security Policy

## Reporting a Vulnerability

Please follow the instructions in the ["How are vulnerabilities and exploits handled?"](https://github.com/stleary/JSON-java/wiki/FAQ#how-are-vulnerabilities-and-exploits-handled) section in the FAQ.

## Verifying Release Signatures

All releases of `org.json:json` published to Maven Central are signed with PGP. The fingerprint, keyserver location, and verification procedure below let you confirm that the artifacts you've downloaded were produced by this project and have not been modified in transit.

### Signing Key

| | |
| --- | --- |
| **Fingerprint** | `FB35 C8D0 2B47 24DA DA23  DE0A FD11 6C19 69FC CFF3` |
| **Long key ID** | `FD116C1969FCCFF3` |
| **Keyserver**   | `hkps://keyserver.ubuntu.com` |

The full 40-character fingerprint above is the canonical identifier for the key. Always pin or compare against the full fingerprint rather than the long or short key ID.

### Importing the Key

```bash
gpg --keyserver hkps://keyserver.ubuntu.com \
    --recv-keys FB35C8D02B4724DADA23DE0AFD116C1969FCCFF3
```

After importing, confirm the fingerprint matches what's published here:

```bash
gpg --fingerprint FB35C8D02B4724DADA23DE0AFD116C1969FCCFF3
```

### Verifying an Artifact

Download both the artifact and its detached signature from Maven Central. For example, for version `20251224`:

```bash
curl -O https://repo1.maven.org/maven2/org/json/json/20251224/json-20251224.jar
curl -O https://repo1.maven.org/maven2/org/json/json/20251224/json-20251224.jar.asc
gpg --verify json-20251224.jar.asc json-20251224.jar
```

A successful verification will report `Good signature from ...` and display the same fingerprint shown above. If GPG reports `BAD signature`, a mismatched fingerprint, or `No public key`, do not use the artifact and please open an issue.

The same procedure applies to the `.pom` and any other signed sidecars in the release directory; substitute the filename you want to verify.

### Gradle Dependency Verification

If you are using Gradle's [dependency verification](https://docs.gradle.org/current/userguide/dependency_verification.html) feature, add an entry like the following to `gradle/verification-metadata.xml`:

```xml
<trusted-key id="FB35C8D02B4724DADA23DE0AFD116C1969FCCFF3" group="org.json" name="json"/>
```

Gradle also accepts the long key ID (`FD116C1969FCCFF3`), but pinning the full fingerprint is recommended.

### Key Rotation

If the signing key is ever rotated or revoked, this document will be updated in the `master` branch with the new fingerprint, and the change will be visible in the file's commit history. Always check this file directly in the repository for the current authoritative value before trusting any third-party copy of the fingerprint.