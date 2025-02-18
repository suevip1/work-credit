package com.anjbo.dao.tools;

import java.util.List;
import java.util.Map;

import com.anjbo.bean.tools.Booking;
import com.anjbo.bean.tools.BookingDetail;

public interface BookingMapper {
	List<Booking> selectBookingList(Booking booking);
	int selectBookingCount(Booking booking);
	Integer bookingExit(Booking booking);
	int addBooking(Booking booking);
	List<BookingDetail> selectBookingDetailList(BookingDetail bookingDetail);
	int selectBookingDetailCount(BookingDetail bookingDetail);
	int addBookingDetail(BookingDetail bookingDetail);
	int updateBookingCode(BookingDetail bookingDetail);
	int updateBookingDetailStatus(BookingDetail bookingDetail);
	BookingDetail bookingDetailExit(BookingDetail bookingDetail);
	int deleteBookingDetail(int id);
	BookingDetail selectBookingDetail(int id);
	int updateBookingDetail(BookingDetail bookingDetail);
	int updateBookingBase(Map<String,Object> param);
}
