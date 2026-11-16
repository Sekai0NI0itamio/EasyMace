# Easy Mace

A client-side Fabric mod for Minecraft 1.21.1+ that makes the Mace easier to use by automatically swapping to it when you attack, so mace damage (Breach / Density) applies to your sword hits.

## Features

### Auto-Swap
When you attack an entity while holding a sword (or any weapon) and a Mace is in your hotbar, Easy Mace automatically swaps to the Mace right before the hit lands, applies the Mace's damage and enchantments, then swaps back to your original item. No manual hotbar switching needed.

### Smart Enchantment Selection
- **Normal hits** (on the ground, not falling): prefers a Mace enchanted with **Breach** (armor penetration)
- **Slam hits** (falling more than the configured threshold): prefers a Mace enchanted with **Density** (fall-damage scaling)

If you only have one Mace, it uses that for both situations.

### Configurable Entity Hitbox Expansion
By default, all entity hitboxes are enlarged **5x** so you can land hits even when not perfectly aimed at the entity. You can:
- Change the multiplier (e.g., 2x, 3x, 10x)
- Disable expansion entirely
- Restrict expansion to specific entity types (e.g., only players)

## Configuration

Config file: `.minecraft/config/easymace.json`

```json
{
  "enabled": true,
  "slamHeightThreshold": 1.5,
  "preferredEnchantForNormal": "breach",
  "preferredEnchantForSlam": "density",
  "hitboxMultiplier": 5.0,
  "hitboxEnabled": true,
  "hitboxAllEntities": true,
  "hitboxEntities": []
}
```

### Commands
- `/easymace reload` — reload config from disk
- `/easymace status` — show current settings

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/) 0.16.14+ for Minecraft 1.21.1+
2. Install [Fabric API](https://modrinth.com/mod/fabric-api)
3. Drop `easymace-*.jar` into your `mods` folder

**Client-side only** — no server installation required.

## Building

This mod is built via GitHub Actions. See `.github/workflows/build.yml`.

## License

MIT
EOF
echo "README created"