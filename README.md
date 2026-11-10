# skiPass

A local CLI password vault built with Java 17. Create a vault with a master
password, then add, list, retrieve, and delete logins or change the master password.
A blank login password generates a random 24-character password.

```sh
./run.sh
# Or choose a vault file (use an absolute path):
./run.sh /absolute/path/personal.skp
./test.sh
```

Requires a JDK 17 or newer. The scripts compile directly without Maven or external
libraries. Run in a real terminal: password input is hidden and redirected input
is refused. The default file is `~/.local/share/skipass/vault.skp`.
Type `quit` to lock the vault and exit. Passwords are revealed only after explicit
confirmation; revealed passwords remain in terminal scrollback.

The entire vault payload, including entry names, is encrypted with AES-256-GCM.
PBKDF2-HMAC-SHA256 derives the encryption key with 600,000 iterations and a fresh
16-byte salt per save. Each save also uses a fresh 12-byte nonce. The format header
is authenticated. Saves use an atomic replacement, with owner-only vault file
permissions on POSIX systems. A file lock prevents simultaneous CLI sessions from
overwriting changes. Filesystems must support atomic replacement.

This is an initial prototype, not an audited password manager. There is no cloud
sync, account service, recovery, or automatic idle lock. Unlocked entries live in
Java memory and cannot all be reliably erased. Keep the vault in a private local
directory and use test credentials during development. Back up the encrypted vault
while skiPass is closed; there is no recovery if you forget the master password.
Changing the master password does not change passwords on older backups.

`./test.sh` checks encrypted round trips, wrong passwords, tampering, truncation,
randomised ciphertext, master-password changes, deletion, and POSIX permissions.
