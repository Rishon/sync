# sync

## Setup

```
$ git clone https://github.com/Rishon/sync
$ cd sync
```

## Build

``
./gradlew clean :shadowJar
``

## Run

Drag the jar from ``build/libs`` into all of your instances.

## List of stuff:

### Done:

- ✅ Folia support
- ✅ Transfer command between instances using transfer packets
- ✅ Add fake player on connect
- ✅ Remove fake player on disconnect
- ✅ Sync player movement (walk, jump, sneak, sprint)
- ✅ Sync player rotation (head + body)
- ✅ Sync player inventory
- ✅ Sync player health
- ✅ Sync player hunger
- ✅ Sync player experience
- ✅ Sync world weather
- ✅ Sync world time
- ✅ Sync world border
- ✅ Sync player chat
- ✅ Sync server online players count
- ✅ Sync player punch animation
- ✅ Sync world difficulty
- ✅ Sync player gamemode
- ✅ Sync player potion effects
- ✅ Sync swimming animation
- ✅ Sync glide animation
- ✅ Fix player connection desync
- ✅ Fix player disconnection desync
- 
### In Progress:

- 🟧 API in progress
- 🟧 Sync player equipment
- 🟧 Sync player damage hit

### To Do:

- ❌ Optimize packet handling
- ❌ Fix redis cache not being deleted upon instance shutdown

#### ⚠️ Probably not going to do any world related stuff (blocks, entities, etc...)