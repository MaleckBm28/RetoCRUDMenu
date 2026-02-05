package model;

/**
 * Enumerado que define los diferentes tipos de juegos de cartas coleccionables 
 * disponibles en el mercado. 
 * * Se utiliza para categorizar los productos y filtrar las búsquedas dentro 
 * de la aplicación.
 * * @author Alex
 * @version 1.0
 */
public enum GameType {
    /** Representa el juego Magic: The Gathering. */
    MAGIC, 
    
    /** Representa el juego Pokémon Trading Card Game. */
    POKEMON, 
    
    /** Representa el juego Yu-Gi-Oh! Trading Card Game. */
    YUGIOH, 
    
    /** Representa el juego Digimon Card Game. */
    DIGIMON, 
    
    /** Representa el juego Shadowverse: Evolve. */
    SHADOWVERSE
}