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
- ✅ Sync world weather (Will not work with Folia)
- ✅ Sync world time (Will not work with Folia)
- ✅ Sync world border (Will not work with Folia)
- ✅ Sync world difficulty (Will not work with Folia)
- ✅ Sync player chat
- ✅ Sync server online players count
- ✅ Sync player punch animation
- ✅ Sync player gamemode
- ✅ Sync player potion effects
- ✅ Sync swimming animation
- ✅ Sync glide animation

### In Progress:

- 🟧 API in progress
- 🟧 Sync player equipment
- 🟧 Sync player damage hit

### To Do:

- ❌ Optimize packet handling

#### ⚠️ Probably not going to do any world related stuff (blocks, entities, etc...)
