package com.b2012202.mxhtknt.Services.Impl;

import com.b2012202.mxhtknt.Request.ResponseObject;
import com.b2012202.mxhtknt.Request.TuyenDuongRequest;
import com.b2012202.mxhtknt.Models.EmbeddedId.XaID;
import com.b2012202.mxhtknt.Models.TuyenDuong;
import com.b2012202.mxhtknt.Models.Xa;
import com.b2012202.mxhtknt.Repositories.TuyenDuongRepository;
import com.b2012202.mxhtknt.Repositories.XaRepository;
import com.b2012202.mxhtknt.Services.TuyenDuongService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TuyenDuongServiceImpl implements TuyenDuongService {
    private final TuyenDuongRepository tuyenDuongRepository;
    private final XaRepository xaRepository;
    @Override
    public ResponseObject createTuyenDuong(TuyenDuongRequest tuyenDuongRequest) {
        TuyenDuong tuyenDuong= tuyenDuongRepository.findById(tuyenDuongRequest.getTenDuong()).orElse(null);
        if(tuyenDuong!=null){
            return new ResponseObject("failed","Tuyen duong already exits", tuyenDuong);
        }
        TuyenDuong saveTuyenDuong= TuyenDuong.builder()
                .tenDuong(tuyenDuongRequest.getTenDuong())
                .build();
        return new ResponseObject("ok","Create TuyenDuong successfully", tuyenDuongRepository.save(saveTuyenDuong));
    }

    @Override
    public ResponseObject getAllTuyenDuong() {
        return new ResponseObject("ok","Get all TuyenDuong", tuyenDuongRepository.findAll());
    }

    @Override
    public ResponseObject addTuyenDuongToXa(TuyenDuongRequest tuyenDuongRequest) {
        XaID xaID= XaID.builder()
                .tenTinh(tuyenDuongRequest.getTenTinh())
                .tenHuyen(tuyenDuongRequest.getTenHuyen())
                .tenXa(tuyenDuongRequest.getTenXa())
                .build();

        Xa exitsXa= xaRepository.findById(xaID).orElse(null);
        TuyenDuong exitsTuyenDuong= tuyenDuongRepository.findById(tuyenDuongRequest.getTenDuong()).orElse(null);
        if(exitsXa!=null && exitsTuyenDuong!=null){
            exitsXa.getTuyenDuongSet().add(exitsTuyenDuong);
            return new ResponseObject("ok", "add tuyenduong to xa successfully",xaRepository.save(exitsXa));
        }
        return new ResponseObject("failed", "info invalid","");
    }


}
