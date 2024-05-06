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

### In Progress:

- 🟧 API in progress

### To Do:

- ❌ Sync world difficulty
- ❌ Sync player gamemode
- ❌ Sync player effects
- ❌ Sync player equipment
- ❌ Sync player damage hit

#### ⚠️ Probably not going to do any world related stuff (blocks, entities, etc...)