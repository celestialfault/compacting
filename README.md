# Compacting

An intelligent compact chat mod for Fabric, largely inspired by the logic of same feature from [Patcher].

Messages are compacted based on how long ago they were last seen, instead of only by comparing the
last few chat messages.

It also intelligently handles dividers:

![](.github/dividers.png)

## Known Issues

- Any mod that adds timestamps in any capacity will very likely break chat compacting.  
  If that's something you find important, you're unfortunately going to have to settle for other mods that do this
  differently, like [Compact Chat].

[Patcher]: https://sk1er.club/mods/patcher
[Compact Chat]: https://modrinth.com/mod/compact-chat
