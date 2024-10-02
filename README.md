
# Streaming Framework

Uma api criada para o Minestom, feita para facilitar a criação de qualquer tipo de display no Minecraft.

## Demo

### Renderizar uma imagem estática em um mapa
```java
new MapRenderer(
    Point,
    Instance,
    Orientation,
    128, // Width
    128, // Height
    20, // Frame rate
    100, // Similarity
    false, // Bundle Packet (Apenas funciona se slowSend tiver desativado)
    false, // Slow Send (Enviar imagens em diversos ticks, pode acelerar usando o método MapRenderer#setAmount(int))
    true // Arb Encode (https://github.com/JNNGL/vanilla-shaders?tab=readme-ov-file#rgb-maps)
).render(BufferedImage)
```

### Fazer Streaming em tempo real da sua tela com partículas
```java
new LocalClient(new ParticleImage(
        Vec.ZERO,
        WorldLoader.instance,
        16 * 8, 9 * 8, 
        0.3f, 1f
)).start();
```
## Gradle
```kts
repositories {
    maven {
        name = "craftsapiensRepoReleases"
        isAllowInsecureProtocol = true
        url = uri("http://node.craftsapiens.com.br:50021/releases")
    }
}

dependencies {
    // Não recomendado usar versões onde o último parâmetro não é zero
    implementation("codes.shiftmc:streaming:1.x.0")
}
```
## Arb Encode

Com Arb
![With arb](https://i.imgur.com/iOVFZTo.jpeg)
Sem Arb
![Without arb](https://i.imgur.com/5gt5WqO.jpeg)
