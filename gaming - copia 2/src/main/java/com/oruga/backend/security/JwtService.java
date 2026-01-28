package com.oruga.backend.security;

import com.oruga.backend.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // ✅ Método sobrecargado para Usuario
    public String generateToken(Usuario usuario) {
        Map<String, Object> extraClaims = new HashMap<>();
        return buildToken(extraClaims, usuario, jwtExpiration);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        Long userId = null;
        String email = userDetails.getUsername();
        Integer cantidadMensajes = 0;
        Integer cantidadPublicaciones = 0;
        Integer cantidadReportes = 0;
        Boolean esModerador = false;
        String nombreUsuario = "";
        
        if (userDetails instanceof Usuario) {
            Usuario usuario = (Usuario) userDetails;
            userId = usuario.getId();
            email = usuario.getEmail();
            nombreUsuario = usuario.getNombreUsuario();
            cantidadMensajes = usuario.getCantidadMensajes() != null ? usuario.getCantidadMensajes() : 0;
            cantidadPublicaciones = usuario.getCantidadPublicaciones() != null ? usuario.getCantidadPublicaciones() : 0;
            cantidadReportes = usuario.getCantidadReportes() != null ? usuario.getCantidadReportes() : 0;
            esModerador = usuario.getEsModerador() != null ? usuario.getEsModerador() : false;
        }
        
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(email)
                .claim("userId", userId)
                .claim("email", email)
                .claim("nombreUsuario", nombreUsuario)
                .claim("cantidadMensajes", cantidadMensajes)
                .claim("cantidadPublicaciones", cantidadPublicaciones)
                .claim("cantidadReportes", cantidadReportes)
                .claim("esModerador", esModerador)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()  // ✅ Versión compatible con JJWT 0.12.x
                .setSigningKey(getSignInKey())  // ✅ setSigningKey funciona aquí
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
