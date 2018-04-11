package com.demo.uaa.uaaserver;

import java.security.PublicKey;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwa.AlgorithmConstraints.ConstraintType;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwk.PublicJsonWebKey;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.ErrorCodes;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class JWT {

	@Autowired
	UserInformationProxy userProxy;

	@Autowired
	private Repository tokendb; // tokendb object of token db class

	@PostMapping("/token/{purpose}")
	public String createJwtToken(@PathVariable String purpose,@RequestBody UserInformation loginInfoInBody) {
		String retrievedInformation = null;
		if(purpose.equals("login")) {
			retrievedInformation = userProxy.getUserInformationn(loginInfoInBody);
			// if user info is not correct then return False
			if (retrievedInformation.equals("Invalid") || retrievedInformation.equals("Not Found")) {
				return "UnAuthorized";
			}
		}
		else if(purpose.equals("register")) {
			retrievedInformation=userProxy.addUserInformation(loginInfoInBody);
			if (retrievedInformation.equals("Already Exists")) {
				return "UnAuthorized";
			}
		}
		else {
			return "Not Found";
		}
		
		RsaJsonWebKey rsaJsonWebKey = null;
		String[] data = retrievedInformation.split(",");
		String uname = data[0];
		String role = null;
		role = data[1];

		// Create a new Json Web Encryption object

		try {
			rsaJsonWebKey = RsaJwkGenerator.generateJwk(2048);
		} catch (JoseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// Give the JWK a Key ID (kid), which is just the polite thing to do
		rsaJsonWebKey.setKeyId("k1");

		// Create the Claims, which will be the content of the JWT
		JwtClaims claims = null;

		// Create the Claims, which will be the content of the JWT
		claims = new JwtClaims();
		claims.setIssuer("Issuer"); // who creates the token and signs it
		claims.setAudience("Audience"); // to whom the token is intended to be sent
		claims.setExpirationTimeMinutesInTheFuture(10); // time when the token will expire (10 minutes from now)
		claims.setGeneratedJwtId(); // a unique identifier for the token
		claims.setIssuedAtToNow(); // when the token was issued/created (now)
		claims.setNotBeforeMinutesInThePast(2); // time before which the token is not yet valid (2 minutes ago)
		claims.setSubject(role); // the subject/principal is whom the token is about
		claims.setClaim("username", uname); // additional claims/attributes about the subject can be added

		// A JWT is a JWS and/or a JWE with JSON claims as the payload.
		// In this example it is a JWS so we create a JsonWebSignature object.
		JsonWebSignature jws = new JsonWebSignature();

		// The payload of the JWS is JSON content of the JWT Claims
		jws.setPayload(claims.toJson());

		// The JWT is signed using the private key
		jws.setKey(rsaJsonWebKey.getPrivateKey());

		// Set the Key ID (kid) header because it's just the polite thing to do.
		// We only have one key in this example but a using a Key ID helps
		// facilitate a smooth key rollover process
		jws.setKeyIdHeaderValue(rsaJsonWebKey.getKeyId());

		// Set the signature algorithm on the JWT/JWS that will integrity protect the
		// claims
		jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);

		// Sign the JWS and produce the compact serialization or the complete JWT/JWS
		// representation, which is a string consisting of three dot ('.') separated
		// base64url-encoded parts in the form Header.Payload.Signature
		// If you wanted to encrypt it, you can simply set this jwt as the payload
		// of a JsonWebEncryption object and set the cty (Content Type) header to "jwt".
		String jwt = null;
		try {
			jwt = jws.getCompactSerialization();
			// The shared secret or shared symmetric key represented as a octet sequence
			// JSON Web Key (JWK)
		} catch (JoseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// A JSON string with only the public key info
		String publicKeyJwkString = rsaJsonWebKey.toJson(JsonWebKey.OutputControlLevel.PUBLIC_ONLY);

		tokendb.save(new UaaModel(publicKeyJwkString, jwt)); // add to database

		return jwt;
	}

	@GetMapping("/verifytoken/{token}")
	public String verifyTokenComingFromService(@PathVariable String token) {
		// public key from token db
		Optional<UaaModel> userFound = tokendb.findById(token);
		if (!userFound.isPresent()) {
			return "false";
		}
		UaaModel uaaModel = userFound.get();
		PublicJsonWebKey parsedPublicKeyJwk = null;
		try {
			parsedPublicKeyJwk = PublicJsonWebKey.Factory.newPublicJwk(uaaModel.getPublicKey());
		} catch (JoseException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		PublicKey publicKey = parsedPublicKeyJwk.getPublicKey();
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
			JwtClaims jwtClaims = jwtConsumer.processToClaims(token);
			System.out.println("JWT validation succeeded! " + jwtClaims);
			try {
				return "true"+","+jwtClaims.getSubject();
			} catch (MalformedClaimException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
					// e1.printStackTrace();
				}
			}

			// Or maybe the audience was invalid
			if (e.hasErrorCode(ErrorCodes.AUDIENCE_INVALID)) {
				try {
					System.out.println("JWT had wrong audience: " + e.getJwtContext().getJwtClaims().getAudience());
				} catch (MalformedClaimException e1) {
					return "UnAuthorized";
					// e1.printStackTrace();
				}
			}
			try {
				throw new Exception("UnAuthorized");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return "false";

	}
}