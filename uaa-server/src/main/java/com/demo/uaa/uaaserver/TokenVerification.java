package com.demo.uaa.uaaserver;

import java.security.PublicKey;

import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwa.AlgorithmConstraints.ConstraintType;
import org.jose4j.jwe.ContentEncryptionAlgorithmIdentifiers;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jwe.KeyManagementAlgorithmIdentifiers;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwk.PublicJsonWebKey;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.ErrorCodes;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

public class TokenVerification {

	@GetMapping("/user/**")
	public String greetUser(@RequestHeader("application-token") String jwe) {
		String acceptedRoles=null;//get after decryption
		System.out.println("****"+jwe);
		
		String jwt = null;
		PublicKey publicKey = null;

		// That other party, the receiver, can then use JsonWebEncryption to decrypt the
		// message.
		JsonWebEncryption receiverJwe = new JsonWebEncryption();

		// Set the algorithm constraints based on what is agreed upon or expected from
		// the sender
		AlgorithmConstraints algConstraints = new AlgorithmConstraints(ConstraintType.WHITELIST,
				KeyManagementAlgorithmIdentifiers.DIRECT);
		receiverJwe.setAlgorithmConstraints(algConstraints);
		AlgorithmConstraints encConstraints = new AlgorithmConstraints(ConstraintType.WHITELIST,
				ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256);
		receiverJwe.setContentEncryptionAlgorithmConstraints(encConstraints);

		// Set the compact serialization on new Json Web Encryption object
		try {
			receiverJwe.setCompactSerialization(jwe);
		} catch (JoseException e2) {
			return "UnAuthorized";
			//e2.printStackTrace();
		}

		// The shared secret or shared symmetric key represented as a octet sequence
		// JSON Web Key (JWK)
		String jwkJson = "{\"kty\":\"oct\",\"k\":\"Fdh9u8rINxfivbrianbbVT1u232VQBZYKx1HGAGPt2I\"}";
		JsonWebKey jwk = null;
		try {
			jwk = JsonWebKey.Factory.newJwk(jwkJson);
		} catch (JoseException e2) {
			return "UnAuthorized";
			//e2.printStackTrace();
		}

		// Symmetric encryption, like we are doing here, requires that both parties have
		// the same key.
		// The key will have had to have been securely exchanged out-of-band somehow.
		receiverJwe.setKey(jwk.getKey());
		// Get the message that was encrypted in the JWE. This step performs the actual
		// decryption steps.
		String plaintext = null;
		try {
			plaintext = receiverJwe.getPlaintextString();
		} catch (JoseException e2) {
			return "UnAuthorized";
			//e2.printStackTrace();
		}
		
		try {
			if (!(plaintext.equals(" "))) {
				String pkey = plaintext.substring(0, plaintext.indexOf("JWT:") - 1);
				PublicJsonWebKey parsedPublicKeyJwk = null;
				try {
					parsedPublicKeyJwk = PublicJsonWebKey.Factory.newPublicJwk(pkey);
				} catch (JoseException e2) {
					return "UnAuthorized";
					//e2.printStackTrace();
				}
				publicKey = parsedPublicKeyJwk.getPublicKey();			
				jwt = plaintext.substring(plaintext.indexOf("JWT:") + 4, plaintext.length());
			}
			
			
			// Use JwtConsumerBuilder to construct an appropriate JwtConsumer, which will
			// be used to validate and process the JWT.
			// The specific validation requirements for a JWT are context dependent,
			// however,
			// it typically advisable to require a (reasonable) expiration time, a trusted
			// issuer, and
			// and audience that identifies your system as the intended recipient.
			// If the JWT is encrypted too, you need only provide a decryption key or
			// decryption key resolver to the builder.
			JwtConsumer jwtConsumer = new JwtConsumerBuilder().setRequireExpirationTime() // the JWT must have an
																							// expiration time
					.setAllowedClockSkewInSeconds(30) // allow some leeway in validating time based claims to account
														// for clock skew
					.setRequireSubject() // the JWT must have a subject claim
					.setExpectedIssuer("Issuer") // whom the JWT needs to have been issued by
					.setExpectedAudience("Audience") // to whom the JWT is intended for
					.setVerificationKey(publicKey) // verify the signature with the public key
					.setJweAlgorithmConstraints( // only allow the expected signature algorithm(s) in the given context
							new AlgorithmConstraints(ConstraintType.WHITELIST, // which is only RS256 here
									AlgorithmIdentifiers.RSA_USING_SHA256))
					.build(); // create the JwtConsumer instance
			try {
				// Validate the JWT and process it to the Claims
				JwtClaims jwtClaims = jwtConsumer.processToClaims(jwt);
				System.out.println("JWT validation succeeded! " + jwtClaims);
				String[] roles = acceptedRoles.split(",");
				for(String role: roles) {
					if(jwtClaims.getSubject().equals(role)) {
						return "Welcome User";
					}
				}
				return "UnAuthorized";
			} catch (InvalidJwtException e) {
				// InvalidJwtException will be thrown, if the JWT failed processing or
				// validation in anyway.
				// Hopefully with meaningful explanations(s) about what went wrong.
				System.out.println("Invalid JWT! " + e);

				// Programmatic access to (some) specific reasons for JWT invalidity is also
				// possible
				// should you want different error handling behavior for certain conditions.

				// Whether or not the JWT has expired being one common reason for invalidity
				if (e.hasExpired()) {
					try {
						System.out.println("JWT expired at " + e.getJwtContext().getJwtClaims().getExpirationTime());
					} catch (MalformedClaimException e1) {
						return "UnAuthorized";
						//e1.printStackTrace();
					}
				}

				// Or maybe the audience was invalid
				if (e.hasErrorCode(ErrorCodes.AUDIENCE_INVALID)) {
					try {
						System.out.println("JWT had wrong audience: " + e.getJwtContext().getJwtClaims().getAudience());
					} catch (MalformedClaimException e1) {
						return "UnAuthorized";
						//						e1.printStackTrace();
					}
				}
				throw new Exception("UnAuthorized");
			}

		} catch (Exception e) {
			return "UnAuthorized";
		}
	}
}
