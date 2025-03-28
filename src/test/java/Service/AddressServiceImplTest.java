package Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.revature.project1.Entities.Address;
import com.revature.project1.repository.AddressRepository;
import com.revature.project1.service.AddressServiceImpl;

@ExtendWith(MockitoExtension.class)
public class AddressServiceImplTest {
    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AddressServiceImpl addressService;

    private Address testAddress;

    @BeforeEach
    public void setUp() {
        testAddress = new Address();
        testAddress.setAddressId(1L);
        testAddress.setCountry("USA");
        testAddress.setState("California");
        testAddress.setCity("Los Angeles");
        testAddress.setStreet("Main St");
        testAddress.setStreetNum("123");
        testAddress.setZip("90001");
    }

    // ShouldReturnListOfAddresses
    @Test
    public void findAllAddress() {
        when(addressRepository.findAll()).thenReturn(List.of(testAddress));       
        List<Address> result = addressService.findAllAddress();
        
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testAddress, result.get(0));
        verify(addressRepository, times(1)).findAll();
    }

    //_ShouldReturnEmpty
    @Test
    public void findAddressById_WhenExists() {
        when(addressRepository.findById(1L)).thenReturn(Optional.of(testAddress));
        
        Optional<Address> result = addressService.findAddressById(1L);
        
        assertTrue(result.isPresent());
        assertEquals(testAddress, result.get());
        verify(addressRepository, times(1)).findById(1L);
    }

    //_ShouldReturnEmpty
    @Test
    public void findAddressById_WhenNotExists() {
        when(addressRepository.findById(99L)).thenReturn(Optional.empty());
        
        Optional<Address> result = addressService.findAddressById(99L);
        
        assertFalse(result.isPresent());
        verify(addressRepository, times(1)).findById(99L);
    }

    //_ShouldReturnSavedAddress
    @Test
    public void testCreateAddress() {
        Address newAddress = new Address();
        newAddress.setAddressId(1L);
        newAddress.setCountry("Mexico");
        newAddress.setCity("CDMX");
        newAddress.setState("Mexico");
        newAddress.setStreet("Masaryk");
        newAddress.setStreetNum("21");
        newAddress.setZip("12345");

        
        when(addressRepository.save(newAddress)).thenReturn(testAddress);
        
        Address result = addressService.createAddress(newAddress);
        
        assertNotNull(result);
        assertEquals(testAddress, result);
        verify(addressRepository, times(1)).save(newAddress);
    }


    @Test
    public void updateAddress() {
        Long addressId = 1L;
        
        Address existingAddress = new Address();
        existingAddress.setAddressId(addressId);
        existingAddress.setCountry("USA");
        existingAddress.setState("California");
        existingAddress.setCity("Los Angeles");
        existingAddress.setStreet("Main St");
        existingAddress.setStreetNum("100");
        existingAddress.setZip("90001");

        Address updateData = new Address();
        updateData.setCountry("Mexico");
        updateData.setState("CDMX");
        updateData.setCity("Ciudad de México");
        updateData.setStreet("Paseo de la Reforma");
        updateData.setStreetNum("500");
        updateData.setZip("06500");

        when(addressRepository.findById(addressId)).thenReturn(Optional.of(existingAddress));
        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Address result = addressService.updateAddress(addressId, updateData);
        assertNotNull(result, "Debería retornar un Address actualizado");
        //assertTrue(result.isPresent(), "Debería retornar un Address actualizado");
        
        Address updatedAddress = result;
        
        assertEquals("Mexico", updatedAddress.getCountry());
        assertEquals("CDMX", updatedAddress.getState());
        assertEquals("Ciudad de México", updatedAddress.getCity());
        assertEquals("Paseo de la Reforma", updatedAddress.getStreet());
        assertEquals("500", updatedAddress.getStreetNum());
        assertEquals("06500", updatedAddress.getZip());
        
        assertEquals(addressId, updatedAddress.getAddressId());

        verify(addressRepository).findById(addressId);
        verify(addressRepository).save(argThat(address -> 
            address.getCountry().equals("Mexico") &&
            address.getStreet().equals("Paseo de la Reforma") &&
            address.getAddressId().equals(addressId)
        ));
    }
}
