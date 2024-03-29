package guru.springframework.spring6restmvc.controllers;

import guru.springframework.spring6restmvc.models.BeerDTO;
import guru.springframework.spring6restmvc.models.BeerStyle;
import guru.springframework.spring6restmvc.services.BeerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
//@RequestMapping("/api/v1/beer")
public class BeerController {

    public static final String BEER_PATH = "/api/v1/beer";
    public static final String BEER_ID_PATH = BEER_PATH + "/{beerId}";

    private final BeerService beerService;

    @PatchMapping(BEER_ID_PATH)
    public ResponseEntity updateBeerPatchById(@PathVariable("beerId") UUID beerId, @Validated @RequestBody BeerDTO beer) {

        beerService.patchBeerById(beerId, beer);

        return new ResponseEntity(HttpStatus.NO_CONTENT);

    }

    @DeleteMapping(BEER_ID_PATH)
    public ResponseEntity deleteById(@PathVariable("beerId") UUID beerId) {

        if (!beerService.deleteById(beerId)) {
            throw new NotFoundException();
        }

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PutMapping(BEER_ID_PATH)
    public ResponseEntity updateById(@PathVariable("beerId") UUID beerId, @Validated @RequestBody BeerDTO beer) {

        if (beerService.updateBeerById(beerId, beer).isEmpty()) {
            throw new NotFoundException();
        }

        return new ResponseEntity(HttpStatus.NO_CONTENT);

    }

    @PostMapping(BEER_PATH)
//    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity handlePost(@Validated @RequestBody BeerDTO beer) {

        BeerDTO savedBeer = beerService.saveNewBeer(beer);

        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.add("Con Moe", "Con Moe");
        httpHeaders.add("Location", BEER_PATH + "/" + savedBeer.getId().toString());

        return new ResponseEntity(httpHeaders, HttpStatus.CREATED);

    }

//    @RequestMapping(method = RequestMethod.GET)
    @GetMapping(value = BEER_PATH)
    public Page<BeerDTO> listBeers(@RequestParam(required = false) String beerName,
                                   @RequestParam(required = false) BeerStyle beerStyle,
                                   @RequestParam(required = false) Boolean showInventory,
                                   @RequestParam(required = false) Integer pageNumber,
                                   @RequestParam(required = false) Integer pageSize){
        return beerService.listBeers(beerName, beerStyle, showInventory, pageNumber, pageSize);
    }

//    @RequestMapping(value = BEER_ID_PATH, method = RequestMethod.GET)
    @GetMapping(value = BEER_ID_PATH)
    public BeerDTO getBeerById(@PathVariable("beerId") UUID beerId) {

        log.debug("Get Beer By Id - in Controller was called!");

        return beerService.getBeerById(beerId).orElseThrow(NotFoundException::new);

    }

//    @ExceptionHandler(NotFoundException.class)
//    public ResponseEntity handleNotFoundException() {
//        System.out.println("In exception handler!!!");
//        System.out.println("ERROR!!!\n404 NOT FOUND");
//
//        return ResponseEntity.notFound().build();
//    }

}
