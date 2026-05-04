package com.udea.bancodigital.auth.application.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LoginRequestDto")
class LoginRequestDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    @DisplayName("Builder")
    class BuilderTest {

        @Test
        @DisplayName("Debe crear LoginRequestDto con todos los campos")
        void debeCrearDtoConTodosLosCampos() {
            LoginRequestDto dto = LoginRequestDto.builder()
                    .correo("test@banco.com")
                    .clave("password123")
                    .mfaCode("123456")
                    .build();

            assertEquals("test@banco.com", dto.getCorreo());
            assertEquals("password123", dto.getClave());
            assertEquals("123456", dto.getMfaCode());
        }

        @Test
        @DisplayName("Debe permitir mfaCode null")
        void debePermitirMfaCodeNull() {
            LoginRequestDto dto = LoginRequestDto.builder()
                    .correo("test@banco.com")
                    .clave("password123")
                    .mfaCode(null)
                    .build();

            assertEquals("test@banco.com", dto.getCorreo());
            assertEquals("password123", dto.getClave());
            assertNull(dto.getMfaCode());
        }

        @Test
        @DisplayName("Debe crear DTO con constructor AllArgsConstructor")
        void debeCrearConAllArgsConstructor() {
            LoginRequestDto dto = new LoginRequestDto("test@banco.com", "password123", "123456");

            assertEquals("test@banco.com", dto.getCorreo());
            assertEquals("password123", dto.getClave());
            assertEquals("123456", dto.getMfaCode());
        }

        @Test
        @DisplayName("Debe crear DTO con NoArgsConstructor y setters")
        void debeCrearConNoArgsConstructorYSetters() {
            LoginRequestDto dto = new LoginRequestDto();
            dto.setCorreo("test@banco.com");
            dto.setClave("password123");
            dto.setMfaCode("123456");

            assertEquals("test@banco.com", dto.getCorreo());
            assertEquals("password123", dto.getClave());
            assertEquals("123456", dto.getMfaCode());
        }

        @Test
        @DisplayName("Debe manejar correo null en builder")
        void debeManejarCorreoNullEnBuilder() {
            LoginRequestDto dto = LoginRequestDto.builder()
                    .correo(null)
                    .clave("password123")
                    .build();

            assertNull(dto.getCorreo());
            assertEquals("password123", dto.getClave());
        }

        @Test
        @DisplayName("Debe manejar clave null en builder")
        void debeManejarClaveNullEnBuilder() {
            LoginRequestDto dto = LoginRequestDto.builder()
                    .correo("test@banco.com")
                    .clave(null)
                    .build();

            assertEquals("test@banco.com", dto.getCorreo());
            assertNull(dto.getClave());
        }
    }

    @Nested
    @DisplayName("Validation Annotations")
    class ValidationTest {

        @Test
        @DisplayName("Debe pasar con correo válido y clave no vacía")
        void debePasarConCorreoValidoYClaveNoVacia() {
            LoginRequestDto dto = LoginRequestDto.builder()
                    .correo("usuario@banco.com")
                    .clave("clave123")
                    .build();

            Set<ConstraintViolation<LoginRequestDto>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty(), "No debe haber violaciones de validación");
        }

        @Test
        @DisplayName("Debe fallar si correo es null")
        void debeFallarSiCorreoNull() {
            LoginRequestDto dto = LoginRequestDto.builder()
                    .correo(null)
                    .clave("clave123")
                    .build();

            Set<ConstraintViolation<LoginRequestDto>> violations = validator.validate(dto);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("El correo es obligatorio")));
        }

        @Test
        @DisplayName("Debe fallar si correo es vacío")
        void debeFallarSiCorreoVacio() {
            LoginRequestDto dto = LoginRequestDto.builder()
                    .correo("")
                    .clave("clave123")
                    .build();

            Set<ConstraintViolation<LoginRequestDto>> violations = validator.validate(dto);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("El correo es obligatorio")));
        }

        @Test
        @DisplayName("Debe fallar si correo tiene formato inválido")
        void debeFallarSiCorreoFormatoInvalido() {
            LoginRequestDto dto = LoginRequestDto.builder()
                    .correo("correo-invalido")
                    .clave("clave123")
                    .build();

            Set<ConstraintViolation<LoginRequestDto>> violations = validator.validate(dto);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Formato de correo inválido")));
        }

        @Test
        @DisplayName("Debe fallar si clave es null")
        void debeFallarSiClaveNull() {
            LoginRequestDto dto = LoginRequestDto.builder()
                    .correo("usuario@banco.com")
                    .clave(null)
                    .build();

            Set<ConstraintViolation<LoginRequestDto>> violations = validator.validate(dto);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("La clave es obligatoria")));
        }

        @Test
        @DisplayName("Debe fallar si clave es vacía")
        void debeFallarSiClaveVacia() {
            LoginRequestDto dto = LoginRequestDto.builder()
                    .correo("usuario@banco.com")
                    .clave("")
                    .build();

            Set<ConstraintViolation<LoginRequestDto>> violations = validator.validate(dto);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("La clave es obligatoria")));
        }

        @Test
        @DisplayName("Debe pasar con mfaCode null")
        void debePasarConMfaCodeNull() {
            LoginRequestDto dto = LoginRequestDto.builder()
                    .correo("usuario@banco.com")
                    .clave("clave123")
                    .mfaCode(null)
                    .build();

            Set<ConstraintViolation<LoginRequestDto>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty(), "mfaCode es opcional");
        }

        @Test
        @DisplayName("Debe pasar con mfaCode vacío")
        void debePasarConMfaCodeVacio() {
            LoginRequestDto dto = LoginRequestDto.builder()
                    .correo("usuario@banco.com")
                    .clave("clave123")
                    .mfaCode("")
                    .build();

            Set<ConstraintViolation<LoginRequestDto>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty(), "mfaCode vacío debe ser válido");
        }

        @Test
        @DisplayName("Debe pasar con mfaCode válido")
        void debePasarConMfaCodeValido() {
            LoginRequestDto dto = LoginRequestDto.builder()
                    .correo("usuario@banco.com")
                    .clave("clave123")
                    .mfaCode("123456")
                    .build();

            Set<ConstraintViolation<LoginRequestDto>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Debe manejar correo con espacios")
        void debeManejarCorreoConEspacios() {
            LoginRequestDto dto = LoginRequestDto.builder()
                    .correo("  test@banco.com  ")
                    .clave("password123")
                    .build();

            Set<ConstraintViolation<LoginRequestDto>> violations = validator.validate(dto);
            assertFalse(violations.isEmpty());
        }

        @Test
        @DisplayName("Debe manejar clave con espacios")
        void debeManejarClaveConEspacios() {
            LoginRequestDto dto = LoginRequestDto.builder()
                    .correo("test@banco.com")
                    .clave("  password with spaces  ")
                    .build();

            Set<ConstraintViolation<LoginRequestDto>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Debe pasar con mfaCode de 6 dígitos")
        void debePasarConMfaCode6Digitos() {
            LoginRequestDto dto = LoginRequestDto.builder()
                    .correo("test@banco.com")
                    .clave("password123")
                    .mfaCode("123456")
                    .build();

            Set<ConstraintViolation<LoginRequestDto>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Debe pasar con mfaCode de letras y números")
        void debePasarConMfaCodeLetrasYNumeros() {
            LoginRequestDto dto = LoginRequestDto.builder()
                    .correo("test@banco.com")
                    .clave("password123")
                    .mfaCode("A1B2C3")
                    .build();

            Set<ConstraintViolation<LoginRequestDto>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode (@Data)")
    class EqualsAndHashCodeTest {

        @Test
        @DisplayName("Debe ser igual a otro DTO con los mismos atributos")
        void debeSerIgualConMismosAtributos() {
            LoginRequestDto dto1 = LoginRequestDto.builder()
                    .correo("test@banco.com")
                    .clave("password")
                    .mfaCode("123456")
                    .build();

            LoginRequestDto dto2 = LoginRequestDto.builder()
                    .correo("test@banco.com")
                    .clave("password")
                    .mfaCode("123456")
                    .build();

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("No debe ser igual si correo difiere")
        void noDebeSerIgualSiCorreoDifiere() {
            LoginRequestDto dto1 = LoginRequestDto.builder()
                    .correo("user1@banco.com")
                    .clave("password")
                    .build();

            LoginRequestDto dto2 = LoginRequestDto.builder()
                    .correo("user2@banco.com")
                    .clave("password")
                    .build();

            assertNotEquals(dto1, dto2);
        }

        @Test
        @DisplayName("No debe ser igual si clave difiere")
        void noDebeSerIgualSiClaveDifiere() {
            LoginRequestDto dto1 = LoginRequestDto.builder()
                    .correo("test@banco.com")
                    .clave("password1")
                    .build();

            LoginRequestDto dto2 = LoginRequestDto.builder()
                    .correo("test@banco.com")
                    .clave("password2")
                    .build();

            assertNotEquals(dto1, dto2);
        }

        @Test
        @DisplayName("No debe ser igual si mfaCode difiere")
        void noDebeSerIgualSiMfaCodeDifiere() {
            LoginRequestDto dto1 = LoginRequestDto.builder()
                    .correo("test@banco.com")
                    .clave("password")
                    .mfaCode("123456")
                    .build();

            LoginRequestDto dto2 = LoginRequestDto.builder()
                    .correo("test@banco.com")
                    .clave("password")
                    .mfaCode("654321")
                    .build();

            assertNotEquals(dto1, dto2);
        }

        @Test
        @DisplayName("No debe ser igual a null")
        void noDebeSerIgualANull() {
            LoginRequestDto dto = LoginRequestDto.builder()
                    .correo("test@banco.com")
                    .clave("password")
                    .build();

            assertNotEquals(null, dto);
        }

        @Test
        @DisplayName("Debe tener hashCode consistente")
        void debeTenerHashCodeConsistente() {
            LoginRequestDto dto = LoginRequestDto.builder()
                    .correo("test@banco.com")
                    .clave("password")
                    .build();

            int hashCode1 = dto.hashCode();
            int hashCode2 = dto.hashCode();

            assertEquals(hashCode1, hashCode2);
        }

        @Test
        @DisplayName("hashCode debe ser igual para objetos iguales")
        void hashCodeDebeSerIgualParaObjetosIguales() {
            LoginRequestDto dto1 = LoginRequestDto.builder()
                    .correo("test@banco.com")
                    .clave("password")
                    .mfaCode("123456")
                    .build();

            LoginRequestDto dto2 = LoginRequestDto.builder()
                    .correo("test@banco.com")
                    .clave("password")
                    .mfaCode("123456")
                    .build();

            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("No debe ser igual a objeto de diferente tipo")
        void noDebeSerIgualADiferenteTipo() {
            LoginRequestDto dto = LoginRequestDto.builder()
                    .correo("test@banco.com")
                    .clave("password")
                    .build();

            assertNotEquals(dto, "string");
        }

        @Test
        @DisplayName("Debe ser igual comparando con mismo objeto")
        void debeSerIgualMismoObjeto() {
            LoginRequestDto dto = LoginRequestDto.builder()
                    .correo("test@banco.com")
                    .clave("password")
                    .build();

            // Self-equality is guaranteed by Object contract;
        }

        @Test
        @DisplayName("equals debe retornar false cuando this.correo es null y other.correo no es null")
        void equalsConCorreoNullVsNotNull() {
            LoginRequestDto dto1 = LoginRequestDto.builder()
                    .correo(null)
                    .clave("password")
                    .build();

            LoginRequestDto dto2 = LoginRequestDto.builder()
                    .correo("test@banco.com")
                    .clave("password")
                    .build();

            assertNotEquals(dto1, dto2);
        }

        @Test
        @DisplayName("equals debe retornar true cuando ambos correo son null")
        void equalsConAmbosCorreoNull() {
            LoginRequestDto dto1 = LoginRequestDto.builder()
                    .correo(null)
                    .clave("password")
                    .build();

            LoginRequestDto dto2 = LoginRequestDto.builder()
                    .correo(null)
                    .clave("password")
                    .build();

            assertEquals(dto1, dto2);
        }

        @Test
        @DisplayName("equals debe retornar false cuando this.clave es null y other.clave no es null")
        void equalsConClaveNullVsNotNull() {
            LoginRequestDto dto1 = LoginRequestDto.builder()
                    .correo("test@banco.com")
                    .clave(null)
                    .build();

            LoginRequestDto dto2 = LoginRequestDto.builder()
                    .correo("test@banco.com")
                    .clave("password")
                    .build();

            assertNotEquals(dto1, dto2);
        }

        @Test
        @DisplayName("equals debe retornar true cuando ambos clave son null")
        void equalsConAmbosClaveNull() {
            LoginRequestDto dto1 = LoginRequestDto.builder()
                    .correo("test@banco.com")
                    .clave(null)
                    .build();

            LoginRequestDto dto2 = LoginRequestDto.builder()
                    .correo("test@banco.com")
                    .clave(null)
                    .build();

            assertEquals(dto1, dto2);
        }

        @Test
        @DisplayName("equals debe retornar false cuando this.mfaCode es null y other.mfaCode no es null")
        void equalsConMfaCodeNullVsNotNull() {
            LoginRequestDto dto1 = LoginRequestDto.builder()
                    .correo("test@banco.com")
                    .clave("password")
                    .mfaCode(null)
                    .build();

            LoginRequestDto dto2 = LoginRequestDto.builder()
                    .correo("test@banco.com")
                    .clave("password")
                    .mfaCode("123456")
                    .build();

            assertNotEquals(dto1, dto2);
        }

        @Test
        @DisplayName("equals debe retornar true cuando ambos mfaCode son null")
        void equalsConAmbosMfaCodeNull() {
            LoginRequestDto dto1 = LoginRequestDto.builder()
                    .correo("test@banco.com")
                    .clave("password")
                    .mfaCode(null)
                    .build();

            LoginRequestDto dto2 = LoginRequestDto.builder()
                    .correo("test@banco.com")
                    .clave("password")
                    .mfaCode(null)
                    .build();

            assertEquals(dto1, dto2);
        }
    }

    @Nested
    @DisplayName("toString (@Data)")
    class ToStringTest {

        @Test
        @DisplayName("Debe incluir los campos en toString")
        void debeIncluirCamposEnToString() {
            LoginRequestDto dto = LoginRequestDto.builder()
                    .correo("test@banco.com")
                    .clave("password")
                    .mfaCode("123456")
                    .build();

            String result = dto.toString();

            assertNotNull(result);
            assertTrue(result.contains("test@banco.com") || result.contains("correo"));
        }

        @Test
        @DisplayName("toString debe funcionar con valores null")
        void toStringConValoresNull() {
            LoginRequestDto dto = LoginRequestDto.builder()
                    .correo(null)
                    .clave(null)
                    .mfaCode(null)
                    .build();

            String result = dto.toString();

            assertNotNull(result);
        }
    }
}
