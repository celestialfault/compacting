# Compacting

**This project now lives on [Codeberg](https://codeberg.org/celestialfault/compacting)**

-----

[![Available on Modrinth](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/available/modrinth_vector.svg)](https://modrinth.com/mod/compacting)

An intelligent compact chat mod for Fabric, largely inspired by the logic of same feature from [Patcher].

Messages are compacted based on how long ago they were last seen, instead of only by comparing the last few chat messages.

It also intelligently handles dividers:

![](.github/dividers.png)

## Known Issues

- Any mod that adds timestamps in any capacity will *probably* break chat compacting.  
  If that's something you find important, you're likely going to have to settle for other mods that do this
  differently, like [Compact Chat] or [Chat Patches].

[Patcher]: https://sk1er.club/mods/patcher
[Compact Chat]: https://modrinth.com/mod/compact-chat
[Chat Patches]: https://modrinth.com/mod/chatpatches
